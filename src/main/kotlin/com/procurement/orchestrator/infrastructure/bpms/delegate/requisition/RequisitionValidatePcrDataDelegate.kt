package com.procurement.orchestrator.infrastructure.bpms.delegate.requisition

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.RequisitionClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.ValidatePcrDataAction
import org.springframework.stereotype.Component

@Component
class RequisitionValidatePcrDataDelegate(
    logger: Logger,
    private val requisitionClient: RequisitionClient,
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

        val tender = context.tryGetTender()
            .orForwardFail { fail -> return fail }

        return requisitionClient.validatePcrData(
            id = commandId,
            params = ValidatePcrDataAction.Params(
                tender = convertToParams(tender)
            )
        )
    }

    private fun convertToParams(tender: Tender): ValidatePcrDataAction.Params.Tender =
        ValidatePcrDataAction.Params.Tender(
            title = tender.title,
            description = tender.description,
            classification = tender.classification
                ?.let { classification ->
                    ValidatePcrDataAction.Params.Tender.Classification(
                        id = classification.id,
                        scheme = classification.scheme
                    )
                },
            awardCriteria = tender.awardCriteria,
            awardCriteriaDetails = tender.awardCriteriaDetails,
            lots = tender.lots
                .map { lot ->
                    ValidatePcrDataAction.Params.Tender.Lot(
                        id = lot.id,
                        internalId = lot.internalId,
                        title = lot.title,
                        description = lot.description,
                        classification = lot.classification
                            ?.let { classification ->
                                ValidatePcrDataAction.Params.Tender.Classification(
                                    id = classification.id,
                                    scheme = classification.scheme
                                )
                            },
                        variants = lot.variants
                            ?.let { variants ->
                                ValidatePcrDataAction.Params.Tender.Lot.Variant(
                                    hasVariants = variants.hasVariants,
                                    variantsDetails = variants.variantDetails
                                )
                            }
                    )
                },
            items = tender.items
                .map { item ->
                    ValidatePcrDataAction.Params.Tender.Item(
                        id = item.id,
                        internalId = item.internalId,
                        description = item.description,
                        quantity = item.quantity,
                        classification = item.classification
                            ?.let { classification ->
                                ValidatePcrDataAction.Params.Tender.Classification(
                                    id = classification.id,
                                    scheme = classification.scheme
                                )
                            },
                        unit = item.unit
                            ?.let { unit ->
                                ValidatePcrDataAction.Params.Tender.Item.Unit(
                                    id = unit.id
                                )
                            },
                        relatedLot = item.relatedLot
                    )
                },
            targets = tender.targets
                .map { target ->
                    ValidatePcrDataAction.Params.Tender.Target(
                        id = target.id,
                        title = target.title,
                        relatesTo = target.relatesTo,
                        relatedItem = target.relatedItem,
                        observations = target.observations
                            .map { observation ->
                                ValidatePcrDataAction.Params.Tender.Target.Observation(
                                    id = observation.id,
                                    period = observation.period
                                        ?.let { period ->
                                            ValidatePcrDataAction.Params.Tender.Target.Observation.Period(
                                                startDate = period.startDate,
                                                endDate = period.endDate
                                            )
                                        },
                                    measure = observation.measure,
                                    notes = observation.notes,
                                    unit = observation.unit
                                        ?.let { unit ->
                                            ValidatePcrDataAction.Params.Tender.Target.Observation.ObservationUnit(
                                                id = unit.id
                                            )
                                        },
                                    dimensions = observation.dimensions
                                        ?.let { dimensions ->
                                            ValidatePcrDataAction.Params.Tender.Target.Observation.Dimensions(
                                                requirementClassIdPR = dimensions.requirementClassIdPR
                                            )
                                        },
                                    relatedRequirementId = observation.relatedRequirementId
                                )
                            }
                    )
                },
            criteria = tender.criteria
                .map { criterion ->
                    ValidatePcrDataAction.Params.Tender.Criterion(
                        id = criterion.id,
                        title = criterion.title,
                        description = criterion.description,
                        relatedItem = criterion.relatedItem,
                        relatesTo = criterion.relatesTo,
                        requirementGroups = criterion.requirementGroups
                            .map { requirementGroup ->
                                ValidatePcrDataAction.Params.Tender.Criterion.RequirementGroup(
                                    id = requirementGroup.id,
                                    description = requirementGroup.description,
                                    requirements = requirementGroup.requirements
                                )
                            }

                    )
                },
            conversions = tender.conversions
                .map { conversion ->
                    ValidatePcrDataAction.Params.Tender.Conversion(
                        id = conversion.id,
                        description = conversion.description,
                        relatedItem = conversion.relatedItem,
                        relatesTo = conversion.relatesTo,
                        rationale = conversion.rationale,
                        coefficients = conversion.coefficients
                            .map { coefficient ->
                                ValidatePcrDataAction.Params.Tender.Conversion.Coefficient(
                                    id = coefficient.id,
                                    value = coefficient.value,
                                    coefficient = coefficient.coefficient
                                )
                            }
                    )
                },
            procurementMethodModalities = tender.procurementMethodModalities,
            documents = tender.documents
                .map { document ->
                    ValidatePcrDataAction.Params.Tender.Document(
                        id = document.id,
                        title = document.title,
                        description = document.description,
                        documentType = document.documentType,
                        relatedLots = document.relatedLots.toList()
                    )
                }
        )
}
