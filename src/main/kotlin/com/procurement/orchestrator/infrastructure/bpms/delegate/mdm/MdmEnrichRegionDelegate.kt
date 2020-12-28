package com.procurement.orchestrator.infrastructure.bpms.delegate.mdm

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.application.client.MdmClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.GlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetSubmissions
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.model.context.members.Errors
import com.procurement.orchestrator.application.model.context.members.Incident
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.extension.date.format
import com.procurement.orchestrator.domain.extension.date.nowDefaultUTC
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.address.Address
import com.procurement.orchestrator.domain.model.address.region.RegionDetails
import com.procurement.orchestrator.domain.model.award.Awards
import com.procurement.orchestrator.domain.model.bid.Bids
import com.procurement.orchestrator.domain.model.bid.BidsDetails
import com.procurement.orchestrator.domain.model.candidate.Candidates
import com.procurement.orchestrator.domain.model.lot.Lots
import com.procurement.orchestrator.domain.model.organization.Organization
import com.procurement.orchestrator.domain.model.organization.Organizations
import com.procurement.orchestrator.domain.model.organization.datail.account.BankAccounts
import com.procurement.orchestrator.domain.model.submission.Details
import com.procurement.orchestrator.domain.model.submission.Submissions
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractBatchRestDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichRegionAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetRegion
import com.procurement.orchestrator.infrastructure.configuration.property.GlobalProperties
import org.springframework.stereotype.Component

@Component
class MdmEnrichRegionDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    transform: Transform,
    private val mdmClient: MdmClient
) : AbstractBatchRestDelegate<MdmEnrichRegionDelegate.Parameters, EnrichRegionAction.Params, List<RegionDetails>>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        const val PARAMETER_NAME_LOCATION: String = "location"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val locations: List<Location> = parameterContainer.getListString(PARAMETER_NAME_LOCATION)
            .orForwardFail { fail -> return fail }
            .map { location ->
                Location.orNull(location)
                    ?: return failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = PARAMETER_NAME_LOCATION,
                            actualValue = location,
                            expectedValues = Location.allowedElements.keysAsStrings()
                        )
                    )
            }
        return success(Parameters(locations))
    }

    override fun prepareSeq(
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<List<EnrichRegionAction.Params>, Fail.Incident> {
        val requestInfo = context.requestInfo

        return parameters.locations
            .flatMap { location ->
                when (location) {
                    Location.SUBMISSION -> getSubmissionAddresses(context)
                    Location.BID -> getBidsAddresses(context)
                    Location.BID_BANK_ACCOUNTS -> getBidsBankAccountAddresses(context)
                    Location.AWARD -> getAwardsAddresses(context)
                    Location.AWARD_BANK_ACCOUNTS -> getAwardsBankAccountAddresses(context)
                    Location.LOT -> getLotsAddresses(context)
                }
                    .orForwardFail { fail -> return fail }
            }
            .toSet()
            .map { regionInfo -> getParams(requestInfo.language, regionInfo) }
            .asSuccess()
    }

    override suspend fun execute(
        elements: List<EnrichRegionAction.Params>,
        executionInterceptor: ExecutionInterceptor
    ): Result<List<RegionDetails>, Fail.Incident> {

        return elements
            .map { params ->
                val result = mdmClient
                    .enrichRegion(params = params)
                    .orForwardFail { error -> return error }

                handleResult(result, executionInterceptor)
            }
            .asSuccess()
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        result: List<RegionDetails>
    ): MaybeFail<Fail.Incident> {

        val regions: Map<RegionDetails, RegionDetails> = result.associateBy { it }

        parameters.locations
            .map { location ->
                when (location) {
                    Location.SUBMISSION -> updateSubmissions(context, regions)
                    Location.BID -> updateBids(context, regions)
                    Location.BID_BANK_ACCOUNTS -> updateBidsBankAccount(context, regions)
                    Location.AWARD -> updateAwards(context, regions)
                    Location.AWARD_BANK_ACCOUNTS -> updateAwardsBankAccount(context, regions)
                    Location.LOT -> updateTenderLots(context, regions)
                }
            }

        return MaybeFail.none()
    }

    private fun updateSubmissions(context: GlobalContext, regions: Map<RegionDetails, RegionDetails>) {
        val updatedSubmissions = context.submissions!!.details
            .map { submission ->
                val updatedCandidates = submission.candidates
                    .map { candidate -> candidate.updateRegion(regions) }
                submission.copy(candidates = Candidates(updatedCandidates))
            }

        context.submissions = Submissions(details = Details(updatedSubmissions))
    }

    private fun updateBids(context: GlobalContext, regions: Map<RegionDetails, RegionDetails>) {
        val updatedBids = context.bids!!.details
            .map { bid ->
                val updatedTenderers = bid.tenderers
                    .map { tenderer -> tenderer.updateRegion(regions) }
                bid.copy(tenderers = Organizations(updatedTenderers))
            }

        context.bids = Bids(
            statistics = context.bids!!.statistics,
            details = BidsDetails(updatedBids)
        )
    }

    private fun updateBidsBankAccount(context: GlobalContext, regions: Map<RegionDetails, RegionDetails>) {
        val updatedBids = context.bids!!.details
            .map { bid ->
                val updatedTenderers = bid.tenderers
                    .map { tenderer ->
                        val updatedDetails = tenderer.details!!
                            .let { details ->
                                val updatedBankAccounts = details.bankAccounts.map { bankAccount ->
                                    val updatedAddress = bankAccount.address!!.updateRegion(regions)
                                    bankAccount.copy(address = updatedAddress)
                                }
                                details.copy(bankAccounts = BankAccounts(updatedBankAccounts))
                            }
                        tenderer.copy(
                            details = updatedDetails
                        )
                    }
                bid.copy(tenderers = Organizations(updatedTenderers))
            }

        context.bids = Bids(
            statistics = context.bids!!.statistics,
            details = BidsDetails(updatedBids)
        )
    }

    private fun updateAwards(context: GlobalContext, countries: Map<RegionDetails, RegionDetails>) {
        val updatedAwards = context.awards
            .map { award ->
                val updatedSuppliers = award.suppliers
                    .map { supplier -> supplier.updateRegion(countries) }
                award.copy(suppliers = Organizations(updatedSuppliers))
            }

        context.awards = Awards(updatedAwards)
    }

    private fun updateAwardsBankAccount(context: GlobalContext, countries: Map<RegionDetails, RegionDetails>) {
        val updatedAwards = context.awards
            .map { award ->
                val updatedSuppliers = award.suppliers
                    .map { supplier ->
                        val updatedDetails = supplier.details!!
                            .let { details ->
                                val updatedBankAccounts = details.bankAccounts.map { bankAccount ->
                                    val updatedAddress = bankAccount.address!!.updateRegion(countries)
                                    bankAccount.copy(address = updatedAddress)
                                }
                                details.copy(bankAccounts = BankAccounts(updatedBankAccounts))
                            }
                        supplier.copy(details = updatedDetails)
                    }
                award.copy(suppliers = Organizations(updatedSuppliers))
            }
        context.awards = Awards(updatedAwards)
    }

    private fun updateTenderLots(context: GlobalContext, regions: Map<RegionDetails, RegionDetails>) {
        val updatedTender = context.tender!!.let {tender ->
            val updatedLots = tender.lots.map { lot ->
                val updatedPlaceOfPerformance = lot.placeOfPerformance.let { placeOfPerformance ->
                    val updatedAddress = placeOfPerformance!!.address!!.updateRegion(regions)
                    placeOfPerformance.copy(address = updatedAddress)
                }
                lot.copy(placeOfPerformance = updatedPlaceOfPerformance)
            }
            tender.copy(lots = Lots(updatedLots))
        }

        context.tender = updatedTender
    }

    private fun Organization.updateRegion(enrichedRegionesById: Map<RegionDetails, RegionDetails>): Organization =
        this.copy(address = this.address?.updateRegion(enrichedRegionesById))

    private fun Address.updateRegion(enrichedRegionesById: Map<RegionDetails, RegionDetails>): Address {
        val oldRegion = this.addressDetails!!.region
        val enrichedRegion = enrichedRegionesById.getValue(oldRegion)
        return this.copy(
            addressDetails = this.addressDetails.copy(
                region = enrichedRegion
            )
        )
    }

    private fun getSubmissionAddresses(context: GlobalContext): Result<List<RegionInfo>, Fail.Incident> {
        val submissions = context.tryGetSubmissions()
            .orForwardFail { fail -> return fail }

        return submissions.details
            .flatMap { submission -> submission.candidates }
            .map { candidate -> getRegionInfo(candidate.address!!) }
            .asSuccess()
    }

    private fun getBidsAddresses(context: GlobalContext): Result<List<RegionInfo>, Fail.Incident> {
        val bids = context.bids
            ?: return failure(Fail.Incident.Bpms.Context.Missing(name = "bids"))

        return bids.details
            .flatMap { bid -> bid.tenderers }
            .map { candidate -> getRegionInfo(candidate.address!!) }
            .asSuccess()
    }

    private fun getBidsBankAccountAddresses(context: GlobalContext): Result<List<RegionInfo>, Fail.Incident> {
        val bids = context.bids
            ?: return failure(Fail.Incident.Bpms.Context.Missing(name = "bids"))

        return bids.details.asSequence()
            .flatMap { bid -> bid.tenderers.asSequence() }
            .map { tenderer -> tenderer.details!! }
            .flatMap { q -> q.bankAccounts.asSequence() }
            .map { bankAccount -> getRegionInfo(bankAccount.address!!) }
            .toList()
            .asSuccess()
    }

    private fun getAwardsAddresses(context: GlobalContext): Result<List<RegionInfo>, Fail.Incident> =
        context.awards.asSequence()
            .flatMap { award -> award.suppliers.asSequence() }
            .map { supplier -> getRegionInfo(supplier.address!!) }
            .toList()
            .asSuccess()

    private fun getAwardsBankAccountAddresses(context: GlobalContext): Result<List<RegionInfo>, Fail.Incident> =
        context.awards.asSequence()
            .flatMap { award -> award.suppliers.asSequence() }
            .map { supplier -> supplier.details!! }
            .flatMap { details -> details.bankAccounts.asSequence() }
            .map { bankAccount -> getRegionInfo(bankAccount.address!!) }
            .toList()
            .asSuccess()

    private fun getLotsAddresses(context: GlobalContext): Result<List<RegionInfo>, Fail.Incident> {
        val tender = context.tryGetTender()
            .orForwardFail { return it }

        return tender.lots.map { lot ->
            val lotAddress = lot.placeOfPerformance!!.address!!
            getRegionInfo(lotAddress)
        }.asSuccess()
    }

    private val getRegionInfo: (Address) -> RegionInfo = { address ->
        val country = address.addressDetails!!.country
        val region = address.addressDetails.region
        RegionInfo(countryId = country.id, regionId = region.id, scheme = region.scheme)
    }

    private fun getParams(language: String, address: RegionInfo): EnrichRegionAction.Params =
        EnrichRegionAction.Params(
            lang = language,
            scheme = address.scheme,
            countryId = address.countryId,
            regionId = address.regionId
        )

    private fun handleResult(
        response: GetRegion.Result,
        executionInterceptor: ExecutionInterceptor
    ): RegionDetails = when (response) {
        is GetRegion.Result.Success -> RegionDetails(
            id = response.id,
            scheme = response.scheme,
            description = response.description,
            uri = response.uri
        )
        is GetRegion.Result.Fail.TranslationNotFound -> {
            executionInterceptor.throwIncident(
                Incident(
                    id = executionInterceptor.processInstanceId,
                    date = nowDefaultUTC().format(),
                    level = Fail.Incident.Level.ERROR.toString(),
                    service = GlobalProperties.service
                        .let { service ->
                            Incident.Service(
                                id = service.id,
                                name = service.name,
                                version = service.version
                            )
                        },
                    details = response.errors
                        .map { incident ->
                            Incident.Detail(
                                code = incident.code,
                                description = incident.description
                            )
                        }
                )
            )
        }
        is GetRegion.Result.Fail.AnotherError -> {
            val errors = response.errors
                .map { error ->
                    Errors.Error(
                        code = error.code,
                        description = error.description
                    )
                }
            executionInterceptor.throwError(errors = errors)
        }
    }

    enum class Location(@JsonValue override val key: String) : EnumElementProvider.Key {

        AWARD("award"),
        AWARD_BANK_ACCOUNTS("award.bankAccounts"),
        BID("bid"),
        BID_BANK_ACCOUNTS("bid.bankAccounts"),
        LOT("lot"),
        SUBMISSION("submission")
        ;

        override fun toString(): String = key

        companion object : EnumElementProvider<Location>(info = info()) {

            @JvmStatic
            @JsonCreator
            fun creator(name: String) = orThrow(name)
        }
    }

    data class Parameters(val locations: List<Location>)
    private data class RegionInfo(val countryId: String, val regionId: String, val scheme: String)
}

