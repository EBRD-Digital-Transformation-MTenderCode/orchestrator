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
import com.procurement.orchestrator.domain.functional.asFailure
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.tender.criteria.Criteria
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
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class MdmGetRequirementsDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    transform: Transform,
    private val mdmClient: MdmClient
) : AbstractBatchRestDelegate<Unit, GetRequirementsAction.Params, Map<RequirementGroupId, List<Requirement>>>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override fun prepareSeq(
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<List<GetRequirementsAction.Params>, Fail.Incident> {

        val requestInfo = context.requestInfo
        val processInfo = context.processInfo

        return context.tryGetTender()
            .orForwardFail { error -> return error }
            .criteria
            .flatMap { criterion ->
                criterion.requirementGroups
                    .map { requirementGroup ->
                        GetRequirementsAction.Params(
                            lang = requestInfo.language,
                            pmd = processInfo.pmd,
                            country = requestInfo.country,
                            phase = processInfo.phase,
                            requirementGroupId = requirementGroup.id
                        )
                    }
            }
            .asSuccess()
    }

    override suspend fun execute(
        elements: List<GetRequirementsAction.Params>,
        executionInterceptor: ExecutionInterceptor
    ): Result<Map<RequirementGroupId, List<Requirement>>, Fail.Incident> = elements
        .map { params ->
            val requirements = mdmClient.getRequirements(params = params)
                .orForwardFail { error -> return error }
                .let {
                    handleResult(it)
                }
            if (requirements.isEmpty())
                return Fail.Incident.Response.Empty(service = ExternalServiceName.MDM, action = "GetRequirements ($params)")
                    .asFailure()

            params.requirementGroupId to requirements
        }
        .toMap()
        .asSuccess()

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        result: Map<RequirementGroupId, List<Requirement>>
    ): MaybeFail<Fail.Incident> {

        if (result.isEmpty())
            return MaybeFail.none()

        val tender = context.tryGetTender()
            .orReturnFail { return MaybeFail.fail(it) }

        val updatedCriteria = tender.criteria
            .map { criterion ->
                val updatedRequirementGroups = criterion.requirementGroups
                    .map { requirementGroup ->
                        val requirements = result.getValue(requirementGroup.id)
                        requirementGroup.copy(requirements = Requirements(requirements))
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
}
