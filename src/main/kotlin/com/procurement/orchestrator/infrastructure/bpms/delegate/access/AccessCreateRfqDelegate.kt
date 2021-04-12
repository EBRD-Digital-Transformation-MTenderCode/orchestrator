package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.model.context.members.Outcomes
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.address.Address
import com.procurement.orchestrator.domain.model.address.AddressDetails
import com.procurement.orchestrator.domain.model.address.country.CountryDetails
import com.procurement.orchestrator.domain.model.address.locality.LocalityDetails
import com.procurement.orchestrator.domain.model.address.region.RegionDetails
import com.procurement.orchestrator.domain.model.classification.Classification
import com.procurement.orchestrator.domain.model.contract.RelatedProcess
import com.procurement.orchestrator.domain.model.contract.RelatedProcessTypes
import com.procurement.orchestrator.domain.model.contract.RelatedProcesses
import com.procurement.orchestrator.domain.model.item.Item
import com.procurement.orchestrator.domain.model.item.Items
import com.procurement.orchestrator.domain.model.lot.Lot
import com.procurement.orchestrator.domain.model.lot.Lots
import com.procurement.orchestrator.domain.model.lot.PlaceOfPerformance
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.domain.model.tender.auction.ElectronicAuctions
import com.procurement.orchestrator.domain.model.tender.auction.ElectronicAuctionsDetail
import com.procurement.orchestrator.domain.model.tender.auction.ElectronicAuctionsDetails
import com.procurement.orchestrator.domain.model.value.Value
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.AccessCommands
import com.procurement.orchestrator.infrastructure.client.web.access.action.CreateRfqAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class AccessCreateRfqDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, CreateRfqAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {
    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        Result.success(Unit)

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<CreateRfqAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo
        val tender = context.tryGetTender()
            .orForwardFail { incident -> return incident }

        return accessClient.createRfq(
            id = commandId,
            params = CreateRfqAction.Params(
                cpid = processInfo.cpid!!,
                relatedCpid = processInfo.relatedProcess!!.cpid,
                relatedOcid = processInfo.relatedProcess.ocid!!,
                additionalCpid = processInfo.additionalProcess!!.cpid!!,
                additionalOcid = processInfo.additionalProcess.ocid!!,
                date = requestInfo.timestamp,
                owner = requestInfo.owner,
                tender = CreateRfqAction.Params.Tender(
                    lots = tender.lots.map { lot ->
                        CreateRfqAction.Params.Tender.Lot(
                            id = lot.id,
                            internalId = lot.internalId,
                            description = lot.description,
                            placeOfPerformance = lot.placeOfPerformance?.let { placeOfPerformance ->
                                CreateRfqAction.Params.Tender.Lot.PlaceOfPerformance(
                                    address = placeOfPerformance.address
                                        ?.let { address ->
                                            CreateRfqAction.Params.Tender.Lot.PlaceOfPerformance.Address(
                                                streetAddress = address.streetAddress,
                                                postalCode = address.postalCode,
                                                addressDetails = address.addressDetails
                                                    ?.let { addressDetails ->
                                                        CreateRfqAction.Params.Tender.Lot.PlaceOfPerformance.Address.AddressDetails(
                                                            country = addressDetails.country
                                                                .let { country ->
                                                                    CreateRfqAction.Params.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Country(
                                                                        scheme = country.scheme,
                                                                        id = country.id,
                                                                        description = country.description,
                                                                        uri = country.uri
                                                                    )
                                                                },
                                                            region = addressDetails.region
                                                                .let { region ->
                                                                    CreateRfqAction.Params.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Region(
                                                                        scheme = region.scheme,
                                                                        id = region.id,
                                                                        description = region.description,
                                                                        uri = region.uri
                                                                    )
                                                                },
                                                            locality = addressDetails.locality
                                                                .let { locality ->
                                                                    CreateRfqAction.Params.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Locality(
                                                                        scheme = locality.scheme,
                                                                        id = locality.id,
                                                                        description = locality.description,
                                                                        uri = locality.uri
                                                                    )
                                                                }
                                                        )
                                                    }
                                            )
                                        },
                                    description = placeOfPerformance.description
                                )
                            },
                            contractPeriod = lot.contractPeriod?.let { period ->
                                CreateRfqAction.Params.Tender.Lot.ContractPeriod(
                                    startDate = period.startDate,
                                    endDate = period.endDate
                                )
                            },
                            title = lot.title,
                            value = lot.value?.let { value ->
                                CreateRfqAction.Params.Tender.Lot.Value(currency = value.currency)
                            }
                        )
                    },
                    items = tender.items.map { item ->
                        CreateRfqAction.Params.Tender.Item(
                            id = item.id,
                            description = item.description,
                            internalId = item.internalId,
                            relatedLot = item.relatedLot,
                            unit = item.unit?.let { unit ->
                                CreateRfqAction.Params.Tender.Item.Unit(
                                    id = unit.id,
                                    name = unit.name
                                )
                            },
                            quantity = item.quantity,
                            classification = item.classification?.let { classification ->
                                CreateRfqAction.Params.Tender.Item.Classification(
                                    id = classification.id,
                                    description = classification.description,
                                    scheme = classification.scheme
                                )
                            }
                        )
                    },
                    procurementMethodModalities = tender.procurementMethodModalities,
                    electronicAuctions = tender.electronicAuctions?.let { electronicAuctions ->
                        CreateRfqAction.Params.Tender.ElectronicAuctions(
                            details = electronicAuctions.details.map { details ->
                                CreateRfqAction.Params.Tender.ElectronicAuctions.Detail(
                                    id = details.id,
                                    relatedLot = details.relatedLot
                                )
                            }
                        )
                    }
                )
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<CreateRfqAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.ACCESS,
                    action = AccessCommands.CreateRfq
                )
            )
        context.processInfo = context.processInfo.copy(ocid = data.ocid)

        val receivedRelatedProcesses = data.relatedProcesses.map { it.convert() }
        context.relatedProcesses = RelatedProcesses(receivedRelatedProcesses)

        val receivedElectronicAuctions = data.tender.electronicAuctions?.details
            ?.map { ElectronicAuctionsDetail(id = it.id, relatedLot = it.relatedLot) }
            .orEmpty()
            .let { ElectronicAuctionsDetails(it) }
            .let { ElectronicAuctions(it) }

        val tender = context.tender ?: Tender()
        val updatedTender = getUpdatedTender(data, tender, receivedElectronicAuctions)

        context.tender = updatedTender
        context.outcomes = createOutcomes(context, data)

        return MaybeFail.none()
    }

    private fun getUpdatedTender(
        data: CreateRfqAction.Result,
        tender: Tender,
        receivedElectronicAuctions: ElectronicAuctions
    ) = data.tender.let { receivedTender ->
        tender.copy(
            id = receivedTender.id,
            status = receivedTender.status,
            statusDetails = receivedTender.statusDetails,
            date = receivedTender.date,
            awardCriteria = receivedTender.awardCriteria,
            awardCriteriaDetails = receivedTender.awardCriteriaDetails,
            lots = receivedTender.lots.map { lot ->
                Lot(
                    id = lot.id,
                    internalId = lot.internalId,
                    description = lot.description,
                    placeOfPerformance = lot.placeOfPerformance.let { placeOfPerformance ->
                        PlaceOfPerformance(
                            address = placeOfPerformance.address
                                .let { address ->
                                    Address(
                                        streetAddress = address.streetAddress,
                                        postalCode = address.postalCode,
                                        addressDetails = address.addressDetails
                                            .let { addressDetails ->
                                                AddressDetails(
                                                    country = addressDetails.country
                                                        .let { country ->
                                                            CountryDetails(
                                                                scheme = country.scheme,
                                                                id = country.id,
                                                                description = country.description,
                                                                uri = country.uri
                                                            )
                                                        },
                                                    region = addressDetails.region
                                                        .let { region ->
                                                            RegionDetails(
                                                                scheme = region.scheme,
                                                                id = region.id,
                                                                description = region.description,
                                                                uri = region.uri
                                                            )
                                                        },
                                                    locality = addressDetails.locality
                                                        .let { locality ->
                                                            LocalityDetails(
                                                                scheme = locality.scheme,
                                                                id = locality.id,
                                                                description = locality.description,
                                                                uri = locality.uri
                                                            )
                                                        }
                                                )
                                            }
                                    )
                                },
                            description = placeOfPerformance.description
                        )
                    },
                    contractPeriod = lot.contractPeriod.let { period ->
                        Period(
                            startDate = period.startDate,
                            endDate = period.endDate
                        )
                    },
                    title = lot.title,
                    value = lot.value.let { value ->
                        Value(currency = value.currency)
                    },
                    statusDetails = lot.statusDetails,
                    status = lot.status
                )
            }.let { Lots(it) },
            items = receivedTender.items.map { item ->
                Item(
                    id = item.id,
                    description = item.description,
                    internalId = item.internalId,
                    relatedLot = item.relatedLot,
                    unit = item.unit.let { unit ->
                        com.procurement.orchestrator.domain.model.unit.Unit(
                            id = unit.id,
                            name = unit.name
                        )
                    },
                    quantity = item.quantity,
                    classification = item.classification.let { classification ->
                        Classification(
                            id = classification.id,
                            description = classification.description,
                            scheme = classification.scheme
                        )
                    }
                )
            }.let { Items(it) },
            electronicAuctions = tender.electronicAuctions?.updateBy(receivedElectronicAuctions)
                    ?: receivedElectronicAuctions.takeIf { it.details.isNotEmpty() }
        )
    }

    private fun CreateRfqAction.Result.RelatedProcess.convert() =
        RelatedProcess(
            id = id,
            identifier = identifier,
            relationship = RelatedProcessTypes(relationship),
            scheme = scheme,
            uri = uri
        )

    private fun createOutcomes(
        context: CamundaGlobalContext,
        result: CreateRfqAction.Result
    ): Outcomes {
        val platformId = context.requestInfo.platformId
        val outcomes = context.outcomes ?: Outcomes()
        val details = outcomes[platformId] ?: Outcomes.Details()

        val newOutcomes = result
            .let { rq -> Outcomes.Details.RequestQuotation(id = rq.ocid) }

        val updatedDetails = details.copy(rq = listOf(newOutcomes))
        outcomes[platformId] = updatedDetails
        return outcomes
    }
}


