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
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.address.Address
import com.procurement.orchestrator.domain.model.address.locality.LocalityDetails
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
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichLocalityAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetLocality
import com.procurement.orchestrator.infrastructure.configuration.property.GlobalProperties
import org.springframework.stereotype.Component

@Component
class MdmEnrichLocalityDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    transform: Transform,
    private val mdmClient: MdmClient
) : AbstractBatchRestDelegate<MdmEnrichLocalityDelegate.Parameters, EnrichLocalityAction.Params, List<LocalityDetails>>(
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
    ): Result<List<EnrichLocalityAction.Params>, Fail.Incident> {
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
            .map { localityInfo -> getParams(requestInfo.language, localityInfo) }
            .asSuccess()
    }

    override suspend fun execute(
        elements: List<EnrichLocalityAction.Params>,
        executionInterceptor: ExecutionInterceptor
    ): Result<List<LocalityDetails>, Fail.Incident> {

        return elements
            .map { params ->
                val response = mdmClient
                    .enrichLocality(params = params)
                    .orForwardFail { error -> return error }

                handleResult(response, executionInterceptor)
            }
            .filter { optionalResult -> optionalResult.isDefined }
            .map { result -> result.get }
            .asSuccess()
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        result: List<LocalityDetails>
    ): MaybeFail<Fail.Incident> {

        val localities = result.associateBy { it }

        parameters.locations
            .map { location ->
                when (location) {
                    Location.SUBMISSION -> updateSubmissions(context, localities)
                    Location.BID -> updateBids(context, localities)
                    Location.BID_BANK_ACCOUNTS -> updateBidsBankAccount(context, localities)
                    Location.AWARD -> updateAwards(context, localities)
                    Location.AWARD_BANK_ACCOUNTS -> updateAwardsBankAccount(context, localities)
                    Location.LOT -> updateTenderLots(context, localities)
                }
            }

        return MaybeFail.none()
    }

    private fun updateSubmissions(context: GlobalContext, localities: Map<LocalityDetails, LocalityDetails>) {
        val updatedSubmissions = context.submissions!!.details
            .map { submission ->
                val updatedCandidates = submission.candidates
                    .map { candidate -> candidate.updateLocality(localities) }
                submission.copy(candidates = Candidates(updatedCandidates))
            }

        context.submissions = Submissions(details = Details(updatedSubmissions))
    }

    private fun updateBids(context: GlobalContext, localities: Map<LocalityDetails, LocalityDetails>) {
        val updatedBids = context.bids!!.details
            .map { bid ->
                val updatedTenderers = bid.tenderers
                    .map { tenderer -> tenderer.updateLocality(localities) }
                bid.copy(tenderers = Organizations(updatedTenderers))
            }

        context.bids = Bids(
            statistics = context.bids!!.statistics,
            details = BidsDetails(updatedBids)
        )
    }

    private fun updateBidsBankAccount(context: GlobalContext, localities: Map<LocalityDetails, LocalityDetails>) {
        val updatedBids = context.bids!!.details
            .map { bid ->
                val updatedTenderers = bid.tenderers
                    .map { tenderer ->
                        val updatedDetails = tenderer.details!!
                            .let { details ->
                                val updatedBankAccounts = details.bankAccounts.map { bankAccount ->
                                    val updatedAddress = bankAccount.address!!.updateLocality(localities)
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

    private fun updateAwards(context: GlobalContext, countries: Map<LocalityDetails, LocalityDetails>) {
        val updatedAwards = context.awards
            .map { award ->
                val updatedSuppliers = award.suppliers
                    .map { supplier -> supplier.updateLocality(countries) }
                award.copy(suppliers = Organizations(updatedSuppliers))
            }

        context.awards = Awards(updatedAwards)
    }

    private fun updateAwardsBankAccount(context: GlobalContext, countries: Map<LocalityDetails, LocalityDetails>) {
        val updatedAwards = context.awards
            .map { award ->
                val updatedSuppliers = award.suppliers
                    .map { supplier ->
                        val updatedDetails = supplier.details!!
                            .let { details ->
                                val updatedBankAccounts = details.bankAccounts.map { bankAccount ->
                                    val updatedAddress = bankAccount.address!!.updateLocality(countries)
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

    private fun updateTenderLots(context: GlobalContext, localities: Map<LocalityDetails, LocalityDetails>) {
        val updatedTender = context.tender!!.let {tender ->
            val updatedLots = tender.lots.map { lot ->
                val updatedPlaceOfPerformance = lot.placeOfPerformance.let { placeOfPerformance ->
                    val updatedAddress = placeOfPerformance!!.address!!.updateLocality(localities)
                    placeOfPerformance.copy(address = updatedAddress)
                }
                lot.copy(placeOfPerformance = updatedPlaceOfPerformance)
            }
            tender.copy(lots = Lots(updatedLots))
        }

        context.tender = updatedTender
    }

    private fun Organization.updateLocality(enrichedLocalitiesById: Map<LocalityDetails, LocalityDetails>): Organization =
        this.copy(address = this.address?.updateLocality(enrichedLocalitiesById))

    private fun Address.updateLocality(enrichedLocalitiesById: Map<LocalityDetails, LocalityDetails>): Address {
        val oldLocality = this.addressDetails!!.locality
        val enrichedLocality = enrichedLocalitiesById[oldLocality]
        return if (enrichedLocality != null)
            this.copy(
                addressDetails = this.addressDetails.copy(
                    locality = enrichedLocality
                )
            ) else this
    }

    private fun getParams(language: String, address: LocalityInfo): EnrichLocalityAction.Params =
        EnrichLocalityAction.Params(
            lang = language,
            scheme = address.scheme,
            countryId = address.countryId,
            regionId = address.regionId,
            localityId = address.localityId
        )

    private fun getSubmissionAddresses(context: GlobalContext): Result<List<LocalityInfo>, Fail.Incident> {
        val submissions = context.tryGetSubmissions()
            .orForwardFail { fail -> return fail }

        return submissions.details
            .flatMap { submission -> submission.candidates }
            .map { candidate -> getLocalityInfo(candidate.address!!) }
            .asSuccess()
    }

    private fun getBidsAddresses(context: GlobalContext): Result<List<LocalityInfo>, Fail.Incident> {
        val bids = context.bids
            ?: return failure(Fail.Incident.Bpms.Context.Missing(name = "bids"))

        return bids.details
            .flatMap { bid -> bid.tenderers }
            .map { candidate -> getLocalityInfo(candidate.address!!) }
            .asSuccess()
    }

    private fun getBidsBankAccountAddresses(context: GlobalContext): Result<List<LocalityInfo>, Fail.Incident> {
        val bids = context.bids
            ?: return failure(Fail.Incident.Bpms.Context.Missing(name = "bids"))

        return bids.details.asSequence()
            .flatMap { bid -> bid.tenderers.asSequence() }
            .map { tenderer -> tenderer.details!! }
            .flatMap { q -> q.bankAccounts.asSequence() }
            .map { bankAccount -> getLocalityInfo(bankAccount.address!!) }
            .toList()
            .asSuccess()
    }

    private fun getAwardsAddresses(context: GlobalContext): Result<List<LocalityInfo>, Fail.Incident> =
        context.awards.asSequence()
            .flatMap { award -> award.suppliers.asSequence() }
            .map { supplier -> getLocalityInfo(supplier.address!!) }
            .toList()
            .asSuccess()

    private fun getAwardsBankAccountAddresses(context: GlobalContext): Result<List<LocalityInfo>, Fail.Incident> =
        context.awards.asSequence()
            .flatMap { award -> award.suppliers.asSequence() }
            .map { supplier -> supplier.details!! }
            .flatMap { details -> details.bankAccounts.asSequence() }
            .map { bankAccount -> getLocalityInfo(bankAccount.address!!) }
            .toList()
            .asSuccess()

    private fun getLotsAddresses(context: GlobalContext): Result<List<LocalityInfo>, Fail.Incident> {
        val tender = context.tryGetTender()
            .orForwardFail { return it }

        return tender.lots.map { lot ->
            val lotAddress = lot.placeOfPerformance!!.address!!
            getLocalityInfo(lotAddress)
        }.asSuccess()
    }

    private val getLocalityInfo: (Address) -> LocalityInfo = { address ->
        val country = address.addressDetails!!.country
        val region = address.addressDetails.region
        val locality = address.addressDetails.locality
        LocalityInfo(
            countryId = country.id,
            regionId = region.id,
            scheme = locality.scheme,
            localityId = locality.id
        )
    }

    private fun handleResult(
        result: GetLocality.Result,
        executionInterceptor: ExecutionInterceptor
    ): Option<LocalityDetails> = when (result) {
        is GetLocality.Result.Success -> Option.pure(
            LocalityDetails(
                id = result.id,
                scheme = result.scheme,
                description = result.description,
                uri = result.uri
            )
        )
        is GetLocality.Result.Fail.SchemeNotFound -> Option.none()
        is GetLocality.Result.Fail.IdNotFound -> {
            val errors = result.details.errors.convertErrors()
            executionInterceptor.throwError(errors = errors)
        }
        is GetLocality.Result.Fail.LanguageNotFound -> {
            val errors = result.details.errors.convertErrors()
            executionInterceptor.throwError(errors = errors)
        }
        is GetLocality.Result.Fail.AnotherError -> {
            val errors = result.details.errors.convertErrors()
            executionInterceptor.throwError(errors = errors)
        }
        is GetLocality.Result.Fail.TranslationNotFound -> {
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
                    details = result.details.errors
                        .map { incident ->
                            Incident.Detail(
                                code = incident.code,
                                description = incident.description
                            )
                        }
                )
            )
        }
    }

    private fun List<EnrichLocalityAction.Response.Error.Details>.convertErrors(): List<Errors.Error> =
        this.map { error ->
            Errors.Error(
                code = error.code,
                description = error.description
            )
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
    private data class LocalityInfo(
        val countryId: String,
        val regionId: String,
        val localityId: String,
        val scheme: String
    )
}

