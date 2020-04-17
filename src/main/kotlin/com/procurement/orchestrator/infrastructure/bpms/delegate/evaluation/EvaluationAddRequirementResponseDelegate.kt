package com.procurement.orchestrator.infrastructure.bpms.delegate.evaluation

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.EvaluationClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.findAwardById
import com.procurement.orchestrator.application.model.context.extension.findRequirementResponseById
import com.procurement.orchestrator.application.model.context.extension.getAwardIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getPartyIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getRequirementResponseIfOnlyOne
import com.procurement.orchestrator.application.model.context.members.Awards
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CreateRequirementResponseAction
import org.springframework.stereotype.Component

@Component
class EvaluationAddRequirementResponseDelegate(
    logger: Logger,
    private val evaluationClient: EvaluationClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, CreateRequirementResponseAction.Result>(
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
    ): Result<Reply<CreateRequirementResponseAction.Result>, Fail.Incident> {

        val award = context.getAwardIfOnlyOne()
            .orForwardFail { fail -> return fail }

        val requirementResponse = award.getRequirementResponseIfOnlyOne()
            .orForwardFail { fail -> return fail }

        val party = context.getPartyIfOnlyOne()
            .orForwardFail { fail -> return fail }

        val processInfo = context.processInfo
        return evaluationClient.createRequirementResponse(
            id = commandId,
            params = CreateRequirementResponseAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                award = CreateRequirementResponseAction.Params.Award(
                    id = award.id,
                    requirementResponse = CreateRequirementResponseAction.Params.Award.RequirementResponse(
                        id = requirementResponse.id as RequirementResponseId.Permanent,
                        value = requirementResponse.value,
                        relatedTenderer = requirementResponse.relatedTenderer
                            ?.let { relatedTenderer ->
                                CreateRequirementResponseAction.Params.Award.RequirementResponse.RelatedTenderer(
                                    id = relatedTenderer.id
                                )
                            },
                        requirement = requirementResponse.requirement
                            ?.let { requirement ->
                                CreateRequirementResponseAction.Params.Award.RequirementResponse.Requirement(
                                    id = requirement.id
                                )
                            },
                        responderer = requirementResponse.responder
                            ?.let {
                                CreateRequirementResponseAction.Params.Award.RequirementResponse.Responder(
                                    id = party.id,
                                    name = party.name
                                )
                            }
                    )

                )
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        data: CreateRequirementResponseAction.Result
    ): MaybeFail<Fail.Incident> {

        val award = context.findAwardById(id = data.award.id)
            .orReturnFail { return MaybeFail.fail(it) }

        val requirementResponse = award.findRequirementResponseById(id = data.award.requirementResponse.id)
            .orReturnFail { return MaybeFail.fail(it) }

        val updatedAward = award.copy(
            requirementResponses = award.requirementResponses
                .map {
                    if (it.id == requirementResponse.id)
                        it.copy(value = data.award.requirementResponse.value)
                    else
                        it
                }
        )

        context.awards = Awards(
            values = context.awards
                .map {
                    if (it.id == data.award.id)
                        updatedAward
                    else
                        award
                }
        )
        return MaybeFail.none()
    }
}
