package com.procurement.orchestrator.infrastructure.bpms.delegate.mdm

import com.procurement.orchestrator.application.client.MdmClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.tender.criteria.Criteria
import com.procurement.orchestrator.domain.model.tender.criteria.CriterionId
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.Requirement
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroupId
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroups
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.Requirements
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractBatchRestDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetRequirements
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetRequirementsAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.convertToGlobalContextEntity
import org.springframework.stereotype.Component

@Component
class MdmGetRequirementsDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    transform: Transform,
    private val mdmClient: MdmClient
) : AbstractBatchRestDelegate<Unit, MdmGetRequirementsDelegate.ParametersDetails, List<MdmGetRequirementsDelegate.RequirementsDetails>>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override fun prepareSeq(
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<List<ParametersDetails>, Fail.Incident> {

        val requestInfo = context.requestInfo
        val processInfo = context.processInfo

        val tender = context.tryGetTender()
            .orForwardFail { error -> return error }

        return tender.criteria
            .map { criterion ->
                criterion.requirementGroups
                    .map { requirementGroup ->
                        ParametersDetails(
                            params = GetRequirementsAction.Params(
                                lang = requestInfo.language,
                                pmd = processInfo.pmd,
                                country = requestInfo.country,
                                phase = processInfo.phase,
                                requirementGroupId = requirementGroup.id
                            ),
                            relatedCriteria = criterion.id
                        )
                    }
            }
            .flatMap { it }
            .asSuccess()
    }

    override suspend fun execute(
        elements: List<ParametersDetails>,
        executionInterceptor: ExecutionInterceptor
    ): Result<List<RequirementsDetails>, Fail.Incident> {

        return elements
            .map { paramsDetails ->
                mdmClient.getRequirements(params = paramsDetails.params)
                    .orForwardFail { error -> return error }
                    .let {
                        val requirement = handleResult(it)
                        RequirementsDetails(
                            relatedCriteria = paramsDetails.relatedCriteria,
                            relatedGroupId = paramsDetails.params.requirementGroupId,
                            requirements = requirement
                        )
                    }
            }
            .asSuccess()
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        result: List<RequirementsDetails>
    ): MaybeFail<Fail.Incident> {

        if (result.isEmpty())
            return MaybeFail.none()

        val tender = context.tryGetTender()
            .orReturnFail { error -> return MaybeFail.fail(error) }

        val dbCriteriaById = tender.criteria
            .associateBy { it.id }

        val requirementsGroupedByCriteriaId = result.groupBy { it.relatedCriteria }

        val updatedCriteria = requirementsGroupedByCriteriaId
            .map { (criterionId, results) ->
                val criterion = dbCriteriaById.getValue(criterionId)

                val dbRequirementGroupsById = criterion.requirementGroups
                    .associateBy { it.id }

                val updatedRequirementGroups = results
                    .map { resultDetails ->
                        dbRequirementGroupsById
                            .getValue(resultDetails.relatedGroupId)
                            .copy(requirements = Requirements(resultDetails.requirements))
                    }
                criterion.copy(requirementGroups = RequirementGroups(updatedRequirementGroups))
            }

        context.tender = tender.copy(criteria = Criteria(updatedCriteria))

        return MaybeFail.none()
    }

    private fun handleResult(response: GetRequirements.Result): List<Requirement> = when (response) {
        is GetRequirements.Result.Success ->
            response.requirements.map { it.convertToGlobalContextEntity() }
    }

    data class RequirementsDetails(
        val relatedCriteria: CriterionId,
        val relatedGroupId: RequirementGroupId,
        val requirements: List<Requirement>
    )

    data class ParametersDetails(
        val params: GetRequirementsAction.Params,
        val relatedCriteria: CriterionId
    )
}

