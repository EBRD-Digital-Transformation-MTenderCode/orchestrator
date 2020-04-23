package com.procurement.orchestrator.infrastructure.bpms.delegate.notice

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.NoticeClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.model.queue.QueueNoticeTask
import com.procurement.orchestrator.infrastructure.bpms.model.queue.grouping
import com.procurement.orchestrator.infrastructure.bpms.model.queue.merge
import com.procurement.orchestrator.infrastructure.bpms.repository.NoticeQueueRepository
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.notice.action.CreateRecordAction
import com.procurement.orchestrator.infrastructure.client.web.notice.action.UpdateRecordAction
import org.springframework.stereotype.Component

@Component
class NoticeDelegate(
    logger: Logger,
    private val noticeClient: NoticeClient,
    operationStepRepository: OperationStepRepository,
    private val noticeQueueRepository: NoticeQueueRepository,
    private val transform: Transform
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

        val requestInfo = context.requestInfo
        val operationId = requestInfo.operationId

        val groupedTask = noticeQueueRepository.load(operationId = operationId)
            .orForwardFail { fail -> return fail }
            .grouping()

        groupedTask.forEach { (id, tasks) ->
            val salt = id.ocid.toString()
            val commandIdWithSalt = commandId + salt
            when (id.action) {
                QueueNoticeTask.Action.UPDATE_RECORD -> {
                    val data = tasks.merge(transform)
                        .orForwardFail { fail -> return fail }
                    val params = UpdateRecordAction.Params(date = requestInfo.timestamp, data = data)
                    val reply = noticeClient.updateRecord(id = commandIdWithSalt, params = params)
                        .orForwardFail { fail -> return fail }

                    when (reply) {
                        is Reply.Success -> Unit
                        is Reply.Errors -> return success(reply)
                        is Reply.Incident -> return success(reply)
                    }
                }
                QueueNoticeTask.Action.CREATE_RECORD -> {
                    val data = tasks.merge(transform)
                        .orForwardFail { fail -> return fail }
                    val params = CreateRecordAction.Params(date = requestInfo.timestamp, data = data)
                    val reply = noticeClient.createRecord(id = commandIdWithSalt, params = params)
                        .orForwardFail { fail -> return fail }

                    when (reply) {
                        is Reply.Success -> Unit
                        is Reply.Errors -> return success(reply)
                        is Reply.Incident -> return success(reply)
                    }
                }
            }
        }

        return success(Reply.None)
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        data: Unit
    ): MaybeFail<Fail.Incident.Bpmn> = MaybeFail.none()
}
