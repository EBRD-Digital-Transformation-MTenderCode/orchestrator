package com.procurement.orchestrator.infrastructure.bpms.delegate.evaluate

import com.procurement.orchestrator.application.client.EvaluateClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.findAwardById
import com.procurement.orchestrator.application.model.context.extension.findRequirementResponseById
import com.procurement.orchestrator.application.model.context.extension.getAwardIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getPartyIfOnlyOne
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
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CreateRequirementResponseAction
import org.springframework.stereotype.Component

@Component
class EvaluateCreateRequirementResponseDelegate(
    logger: Logger,
    private val evaluateClient: EvaluateClient,
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
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<CreateRequirementResponseAction.Result>, Fail.Incident> {

        val award = context.getAwardIfOnlyOne()
            .doOnError { return failure(it) }
            .get

        val requirementResponse = award.getRequirementResponseIfOnlyOne()
            .doOnError { return failure(it) }
            .get

        val party = context.getPartyIfOnlyOne()
            .doOnError { return failure(it) }
            .get

        val processInfo = context.processInfo
        return evaluateClient.createRequirementResponse(
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
            .doOnError { return MaybeFail.fail(it) }
            .get

        val requirementResponse = award.findRequirementResponseById(id = data.award.requirementResponse.id)
            .doOnError { return MaybeFail.fail(it) }
            .get

        val updatedAward = award.copy(
            requirementResponses = award.requirementResponses
                .map {
                    if (it.id == requirementResponse.id)
                        it.copy(value = data.award.requirementResponse.value)
                    else
                        it
                }
        )

        val updatedAwards = context.awards
            .map {
                if (it.id == data.award.id)
                    updatedAward
                else
                    award
            }

        context.awards = updatedAwards
        return MaybeFail.none()
    }
}
