package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.measure.Quantity
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.action.ValidateRfqDataAction
import org.springframework.stereotype.Component

@Component
class AccessValidateRfqDataDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, Unit>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<Unit>, Fail.Incident> {

        val processInfo = context.processInfo
        val tender = context.tryGetTender()
            .orForwardFail { failure -> return failure }

        return accessClient.validateRfqData(
            id = commandId,
            params = ValidateRfqDataAction.Params(
                relatedCpid = processInfo.relatedProcess!!.cpid,
                relatedOcid = processInfo.relatedProcess.ocid!!,
                tender = ValidateRfqDataAction.Params.Tender(
                    lots = tender.lots.map { lot ->
                        ValidateRfqDataAction.Params.Tender.Lot(
                            id = lot.id,
                            value = lot.value?.let { value ->
                                ValidateRfqDataAction.Params.Tender.Lot.Value(
                                    currency = value.currency
                                )
                            },
                            title = lot.title,
                            internalId = lot.internalId,
                            description = lot.description,
                            contractPeriod = lot.contractPeriod?.let { period ->
                                ValidateRfqDataAction.Params.Tender.Lot.ContractPeriod(
                                    startDate = period.startDate,
                                    endDate = period.endDate
                                )
                            },
                            placeOfPerformance = lot.placeOfPerformance?.let { placeOfPerformance ->
                                ValidateRfqDataAction.Params.Tender.Lot.PlaceOfPerformance(
                                    address = placeOfPerformance.address
                                        ?.let { address ->
                                            ValidateRfqDataAction.Params.Tender.Lot.PlaceOfPerformance.Address(
                                                streetAddress = address.streetAddress,
                                                postalCode = address.postalCode,
                                                addressDetails = address.addressDetails
                                                    ?.let { addressDetails ->
                                                        ValidateRfqDataAction.Params.Tender.Lot.PlaceOfPerformance.Address.AddressDetails(
                                                            country = addressDetails.country
                                                                .let { country ->
                                                                    ValidateRfqDataAction.Params.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Country(
                                                                        scheme = country.scheme,
                                                                        id = country.id,
                                                                        description = country.description
                                                                    )
                                                                },
                                                            region = addressDetails.region
                                                                .let { region ->
                                                                    ValidateRfqDataAction.Params.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Region(
                                                                        scheme = region.scheme,
                                                                        id = region.id,
                                                                        description = region.description
                                                                    )
                                                                },
                                                            locality = addressDetails.locality
                                                                .let { locality ->
                                                                    ValidateRfqDataAction.Params.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Locality(
                                                                        scheme = locality.scheme,
                                                                        id = locality.id,
                                                                        description = locality.description
                                                                    )
                                                                }
                                                        )
                                                    }
                                            )
                                        },
                                    description = placeOfPerformance.description
                                )
                            }
                        )
                    },
                    items = tender.items.map { item ->
                        ValidateRfqDataAction.Params.Tender.Item(
                            id = item.id,
                            description = item.description,
                            internalId = item.internalId,
                            classification = item.classification
                                ?.let { classification ->
                                    ValidateRfqDataAction.Params.Tender.Item.Classification(
                                        id = classification.id,
                                        scheme = classification.scheme
                                    )
                                },
                            quantity = item.quantity
                                ?.let { quantity ->
                                    Quantity(quantity.value)
                                },
                            relatedLot = item.relatedLot,
                            unit = item.unit?.let { unit ->
                                ValidateRfqDataAction.Params.Tender.Item.Unit(unit.id)
                            }
                        )
                    },
                    electronicAuctions = tender.electronicAuctions
                        ?.let { electronicAuctions ->
                            ValidateRfqDataAction.Params.Tender.ElectronicAuctions(
                                details = electronicAuctions.details
                                    .map { detail ->
                                        ValidateRfqDataAction.Params.Tender.ElectronicAuctions.Detail(
                                            id = detail.id,
                                            relatedLot = detail.relatedLot
                                        )
                                    }
                            )
                        },
                    procurementMethodModalities = tender.procurementMethodModalities,
                    tenderPeriod = tender.tenderPeriod
                        ?.let { period ->
                            ValidateRfqDataAction.Params.Tender.TenderPeriod(endDate = period.endDate)
                        }

                )
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<Unit>
    ): MaybeFail<Fail.Incident.Bpmn> = MaybeFail.none()
}
