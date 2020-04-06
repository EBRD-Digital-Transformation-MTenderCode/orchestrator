package com.procurement.orchestrator.infrastructure.bpms.delegate.revision

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.RevisionClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getAmendmentIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.amendment.AmendmentId
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.revision.action.CheckAccessToAmendmentAction
import org.springframework.stereotype.Component

@Component
class RevisionCheckAccessToAmendmentDelegate(
    logger: Logger,
    private val revisionClient: RevisionClient,
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
        val cpid: Cpid = processInfo.cpid
        val ocid: Ocid = processInfo.ocid

        val tender = context.tryGetTender()
            .orReturnFail { return failure(it) }

        val amendment = tender.getAmendmentIfOnlyOne()
            .orReturnFail { return failure(it) }

        return revisionClient.checkAccessToAmendment(
            id = commandId,
            params = CheckAccessToAmendmentAction.Params(
                cpid = cpid,
                ocid = ocid,
                token = tender.token,
                owner = tender.owner,
                id = amendment.id as AmendmentId.Permanent
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        data: Unit
    ): MaybeFail<Fail.Incident.Bpmn> = MaybeFail.none()
}
