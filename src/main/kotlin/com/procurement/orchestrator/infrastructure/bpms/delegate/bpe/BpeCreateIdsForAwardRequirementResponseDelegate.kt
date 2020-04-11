package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe

import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getAwardsIfNotEmpty
import com.procurement.orchestrator.application.model.context.members.Awards
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asOption
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractInternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.springframework.stereotype.Component

@Component
class BpeCreateIdsForAwardRequirementResponseDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractInternalDelegate<Unit, Map<RequirementResponseId.Temporal, RequirementResponseId.Permanent>>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Option<Map<RequirementResponseId.Temporal, RequirementResponseId.Permanent>>, Fail.Incident> {
        val awards = context.getAwardsIfNotEmpty()
            .orForwardFail { fail -> return fail }

        return awards.asSequence()
            .flatMap { award ->
                award.requirementResponses.asSequence()
                    .map { requirementResponse ->
                        val temporal = requirementResponse.id as RequirementResponseId.Temporal
                        val permanent = RequirementResponseId.Permanent.generate() as RequirementResponseId.Permanent
                        temporal to permanent
                    }
            }
            .toMap()
            .asOption()
            .asSuccess()
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        data: Map<RequirementResponseId.Temporal, RequirementResponseId.Permanent>
    ): MaybeFail<Fail.Incident.Bpmn> {
        val awards = context.awards

        val updatedAwards = Awards(
            values = awards
                .map { award ->
                    val updatedRequirementResponses = award.requirementResponses
                        .map { requirementResponse ->
                            requirementResponse.copy(
                                id = data.getValue(requirementResponse.id as RequirementResponseId.Temporal)
                            )
                        }
                    award.copy(
                        requirementResponses = updatedRequirementResponses
                    )
                }
        )
        context.awards = updatedAwards

        return MaybeFail.none()
    }
}
