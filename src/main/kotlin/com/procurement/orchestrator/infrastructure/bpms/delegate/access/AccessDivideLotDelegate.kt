package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.model.context.members.ProcessInfo
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
import com.procurement.orchestrator.domain.model.classification.Classifications
import com.procurement.orchestrator.domain.model.item.Item
import com.procurement.orchestrator.domain.model.item.Items
import com.procurement.orchestrator.domain.model.lot.Lot
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.lot.Lots
import com.procurement.orchestrator.domain.model.lot.PlaceOfPerformance
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.domain.model.value.Value
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.AccessCommands
import com.procurement.orchestrator.infrastructure.client.web.access.action.DivideLotAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class AccessDivideLotDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, DivideLotAction.Result>(
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
    ): Result<Reply<DivideLotAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        val tender = context.tryGetTender()
            .orForwardFail { failure -> return failure }

        val dividedLot = generateDividedLot(processInfo)

        return accessClient.divideLot(
            id = commandId,
            params = DivideLotAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                tender = DivideLotAction.Params.Tender(
                    tender.lots
                        .map { lot -> lot.convert() }
                        .let { it + dividedLot },
                    items = tender.items.map { item ->
                        DivideLotAction.Params.Tender.Item(
                            id = item.id,
                            relatedLot = item.relatedLot
                        )
                    }
                )

            )
        )
    }

    private fun generateDividedLot(processInfo: ProcessInfo) =
        DivideLotAction.Params.Tender.Lot(
            id = LotId.create(processInfo.entityId!!),
            description = null,
            title = null,
            value = null,
            internalId = null,
            placeOfPerformance = null,
            contractPeriod = null
        )

    private fun Lot.convert() = DivideLotAction.Params.Tender.Lot(
        id = id,
        title = title,
        description = description,
        internalId = internalId,
        value = value?.let { value ->
            DivideLotAction.Params.Tender.Lot.Value(
                amount = value.amount,
                currency = value.currency
            )
        },
        contractPeriod = contractPeriod
            ?.let { contractPeriod ->
                DivideLotAction.Params.Tender.Lot.ContractPeriod(
                    startDate = contractPeriod.startDate,
                    endDate = contractPeriod.endDate
                )
            },
        placeOfPerformance = placeOfPerformance
            ?.let { placeOfPerformance ->
                DivideLotAction.Params.Tender.Lot.PlaceOfPerformance(
                    address = placeOfPerformance.address
                        ?.let { address ->
                            DivideLotAction.Params.Tender.Lot.PlaceOfPerformance.Address(
                                streetAddress = address.streetAddress,
                                postalCode = address.postalCode,
                                addressDetails = address.addressDetails
                                    ?.let { addressDetails ->
                                        DivideLotAction.Params.Tender.Lot.PlaceOfPerformance.Address.AddressDetails(
                                            country = addressDetails.country
                                                .let { country ->
                                                    DivideLotAction.Params.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Country(
                                                        scheme = country.scheme,
                                                        id = country.id,
                                                        description = country.description,
                                                        uri = country.uri
                                                    )
                                                },
                                            region = addressDetails.region
                                                .let { region ->
                                                    DivideLotAction.Params.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Region(
                                                        scheme = region.scheme,
                                                        id = region.id,
                                                        description = region.description,
                                                        uri = region.uri
                                                    )
                                                },
                                            locality = addressDetails.locality
                                                .let { locality ->
                                                    DivideLotAction.Params.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Locality(
                                                        scheme = locality.scheme,
                                                        id = locality.id,
                                                        description = locality.description,
                                                        uri = locality.uri
                                                    )
                                                }
                                        )
                                    }
                            )
                        }
                )
            }
    )

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<DivideLotAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(service = ExternalServiceName.ACCESS, action = AccessCommands.DivideLot)
            )

        val lots = data.tender.lots
            .map { lot -> lot.toDomain() }
            .let { Lots(it) }

        val items = data.tender.items
            .map { item -> item.toDomain() }
            .let { Items(it) }

        context.tender = context.tender?.copy(lots = lots, items = items) ?: Tender(lots = lots, items = items)

        return MaybeFail.none()
    }
}

private fun DivideLotAction.Result.Tender.Lot.toDomain(): Lot =
    Lot(
        id = id,
        description = description,
        internalId = internalId,
        title = title,
        status = status,
        statusDetails = statusDetails,
        value = Value(
            amount = value.amount,
            currency = value.currency
        ),
        contractPeriod = Period(
            startDate = contractPeriod.startDate,
            endDate = contractPeriod.endDate
        ),
        placeOfPerformance = PlaceOfPerformance(
            address = placeOfPerformance.address.let { address ->
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
            }
        )
    )

private fun DivideLotAction.Result.Tender.Item.toDomain(): Item =
    Item(
        id = id,
        internalId = internalId,
        classification = classification.let { classification ->
            Classification(
                id = classification.id,
                scheme = classification.scheme,
                description = classification.description
            )
        },
        additionalClassifications = additionalClassifications
            ?.map { additionalClassification ->
                Classification(
                    id = additionalClassification.id,
                    description = additionalClassification.description,
                    scheme = additionalClassification.scheme
                )
            }.orEmpty()
            .let { Classifications(it) },
        quantity = quantity,
        unit = unit.let { unit ->
            com.procurement.orchestrator.domain.model.unit.Unit(
                id = unit.id,
                name = unit.name
            )
        },
        description = description,
        relatedLot = relatedLot
    )

