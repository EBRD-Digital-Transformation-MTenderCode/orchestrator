package com.procurement.orchestrator.infrastructure.bpms.delegate.evaluation

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.EvaluationClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getAwardIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getRequirementResponseIfOnlyOne
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.AddRequirementResponseAction
import org.springframework.stereotype.Component

@Component
class EvaluationAddRequirementResponseDelegate(
    logger: Logger,
    private val evaluationClient: EvaluationClient,
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

        val award = context.getAwardIfOnlyOne()
            .orForwardFail { fail -> return fail }

        val requirementResponse = award.getRequirementResponseIfOnlyOne()
            .orForwardFail { fail -> return fail }

        val processInfo = context.processInfo
        return evaluationClient.addRequirementResponse(
            id = commandId,
            params = AddRequirementResponseAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                award = AddRequirementResponseAction.Params.Award(
                    id = award.id,
                    requirementResponse = AddRequirementResponseAction.Params.Award.RequirementResponse(
                        id = requirementResponse.id as RequirementResponseId.Permanent,
                        value = requirementResponse.value
                            ?: return failure(
                                Fail.Incident.Bpms.Context.Missing(
                                    name = "value",
                                    path = "awards.requirementResponse"
                                )
                            ),
                        relatedTenderer = requirementResponse.relatedTenderer
                            ?.let { relatedTenderer ->
                                AddRequirementResponseAction.Params.Award.RequirementResponse.RelatedTenderer(
                                    id = relatedTenderer.id
                                )
                            }
                            ?: return failure(
                                Fail.Incident.Bpms.Context.Missing(
                                    name = "relatedTenderer",
                                    path = "awards.requirementResponse"
                                )
                            ),
                        requirement = requirementResponse.requirement
                            ?.let { requirement ->
                                AddRequirementResponseAction.Params.Award.RequirementResponse.Requirement(
                                    id = requirement.id
                                )
                            }
                            ?: return failure(
                                Fail.Incident.Bpms.Context.Missing(
                                    name = "requirement",
                                    path = "awards.requirementResponse"
                                )
                            ),
                        responder = requirementResponse.responder
                            ?.let { responder ->
                                AddRequirementResponseAction.Params.Award.RequirementResponse.Responder(
                                    identifier = responder.identifier
                                        .let { identifier ->
                                            AddRequirementResponseAction.Params.Award.RequirementResponse.Responder.Identifier(
                                                scheme = identifier.scheme,
                                                id = identifier.id
                                            )
                                        },
                                    name = responder.name
                                        ?: return failure(
                                            Fail.Incident.Bpms.Context.Missing(
                                                name = "name",
                                                path = "awards.requirementResponse.responder"
                                            )
                                        )
                                )
                            }
                            ?: return failure(
                                Fail.Incident.Bpms.Context.Missing(
                                    name = "requirement",
                                    path = "awards.requirementResponse"
                                )
                            )
                    )
                )
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        data: Unit
    ): MaybeFail<Fail.Incident> = MaybeFail.none()
}
