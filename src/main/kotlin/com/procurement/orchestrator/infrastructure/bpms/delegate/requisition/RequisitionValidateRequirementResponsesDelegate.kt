package com.procurement.orchestrator.infrastructure.bpms.delegate.requisition

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.RequisitionClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getBidsDetailIfOnlyOne
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.ValidateRequirementResponsesAction
import org.springframework.stereotype.Component

@Component
class RequisitionValidateRequirementResponsesDelegate(
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

        val processInfo = context.processInfo

        val bid = context.bids.getBidsDetailIfOnlyOne()
            .orForwardFail { fail -> return fail }

        return requisitionClient.validateRequirementResponses(
            id = commandId,
            params = ValidateRequirementResponsesAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                bids = ValidateRequirementResponsesAction.Params.Bids(
                    listOf(
                        ValidateRequirementResponsesAction.Params.Bids.Detail(
                            id = bid.id,
                            relatedLots = bid.relatedLots
                        )
                    )
                ),
                tender = ValidateRequirementResponsesAction.Params.Tender(
                    bid.requirementResponses.map { requirementResponse ->
                        ValidateRequirementResponsesAction.Params.Tender.RequirementResponse(
                            id = requirementResponse.id,
                            value = requirementResponse.value,
                            requirement = ValidateRequirementResponsesAction.Params.Tender.RequirementResponse.Requirement(
                                requirementResponse.requirement?.id
                            ),
                            period = ValidateRequirementResponsesAction.Params.Tender.RequirementResponse.Period(
                                startDate = requirementResponse.period?.startDate,
                                endDate = requirementResponse.period?.endDate
                            )
                        )
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
