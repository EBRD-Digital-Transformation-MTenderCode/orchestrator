package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.action.ValidateLotsDataForDivisionAction
import org.springframework.stereotype.Component

@Component
class AccessValidateLotsDataForDivisionDelegate(
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

        return accessClient.validateLotsDataForDivision(
            id = commandId,
            params = ValidateLotsDataForDivisionAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                tender = ValidateLotsDataForDivisionAction.Params.Tender(
                    tender.lots.map { lot ->
                        ValidateLotsDataForDivisionAction.Params.Tender.Lot(
                            id = lot.id,
                            title = lot.title,
                            description = lot.description,
                            internalId = lot.internalId,
                            value = lot.value?.let { value ->
                                ValidateLotsDataForDivisionAction.Params.Tender.Lot.Value(
                                    amount = value.amount,
                                    currency = value.currency
                                )
                            },
                            contractPeriod = lot.contractPeriod
                                ?.let { contractPeriod ->
                                    ValidateLotsDataForDivisionAction.Params.Tender.Lot.ContractPeriod(
                                        startDate = contractPeriod.startDate,
                                        endDate = contractPeriod.endDate
                                    )
                                },
                            placeOfPerformance = lot.placeOfPerformance?.let { placeOfPerformance ->
                                ValidateLotsDataForDivisionAction.Params.Tender.Lot.PlaceOfPerformance(
                                    address = placeOfPerformance.address
                                        ?.let { address ->
                                            ValidateLotsDataForDivisionAction.Params.Tender.Lot.PlaceOfPerformance.Address(
                                                streetAddress = address.streetAddress,
                                                postalCode = address.postalCode,
                                                addressDetails = address.addressDetails
                                                    ?.let { addressDetails ->
                                                        ValidateLotsDataForDivisionAction.Params.Tender.Lot.PlaceOfPerformance.Address.AddressDetails(
                                                            country = addressDetails.country
                                                                .let { country ->
                                                                    ValidateLotsDataForDivisionAction.Params.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Country(
                                                                        scheme = country.scheme,
                                                                        id = country.id,
                                                                        description = country.description
                                                                    )
                                                                },
                                                            region = addressDetails.region
                                                                .let { region ->
                                                                    ValidateLotsDataForDivisionAction.Params.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Region(
                                                                        scheme = region.scheme,
                                                                        id = region.id,
                                                                        description = region.description
                                                                    )
                                                                },
                                                            locality = addressDetails.locality
                                                                .let { locality ->
                                                                    ValidateLotsDataForDivisionAction.Params.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Locality(
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
                        ValidateLotsDataForDivisionAction.Params.Tender.Item(
                            id = item.id,
                            relatedLot = item.relatedLot
                        )
                    }
                )
            )
        )
    }
}
