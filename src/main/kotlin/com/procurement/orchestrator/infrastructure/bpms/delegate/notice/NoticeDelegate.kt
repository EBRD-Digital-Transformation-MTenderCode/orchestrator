package com.procurement.orchestrator.infrastructure.bpms.delegate.notice

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.NoticeClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractSeqExternalDelegate
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
import java.time.LocalDateTime

@Component
class NoticeDelegate(
    logger: Logger,
    private val noticeClient: NoticeClient,
    operationStepRepository: OperationStepRepository,
    private val noticeQueueRepository: NoticeQueueRepository,
    private val transform: Transform
) : AbstractSeqExternalDelegate<Unit, NoticeDelegate.Task, Unit>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun prepareParameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override fun prepareSeq(
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<List<Task>, Fail.Incident> {
        val requestInfo = context.requestInfo
        val operationId = requestInfo.operationId

        return noticeQueueRepository.load(operationId = operationId)
            .orForwardFail { fail -> return fail }
            .grouping()
            .map { (id, tasks) ->
                tasks.merge(transform)
                    .orForwardFail { fail -> return fail }
                    .let { data ->
                        Task(
                            action = id.action,
                            salt = id.cpid.toString() + id.ocid.toString() + id.action.toString(),
                            date = requestInfo.timestamp,
                            data = data
                        )
                    }
            }
            .asSuccess()
    }

    override suspend fun call(
        baseCommandId: CommandId,
        element: Task
    ): Result<Reply<Unit>, Fail.Incident> {

        val commandIdWithSalt = baseCommandId + element.salt
        val reply = when (element.action) {
            QueueNoticeTask.Action.UPDATE_RECORD -> {
                val params = UpdateRecordAction.Params(date = element.date, data = element.data)
                noticeClient.updateRecord(id = commandIdWithSalt, params = params)
                    .orForwardFail { fail -> return fail }
            }

            QueueNoticeTask.Action.CREATE_RECORD -> {
                val params = CreateRecordAction.Params(date = element.date, data = element.data)
                noticeClient.createRecord(id = commandIdWithSalt, params = params)
                    .orForwardFail { fail -> return fail }
            }
        }

        return when (reply) {
            is Reply.None -> success(Reply.None)
            is Reply.Success -> success(Reply.None)
            is Reply.Errors -> success(reply)
            is Reply.Incident -> success(reply)
        }
    }

    class Task(
        val action: QueueNoticeTask.Action,
        val salt: String,
        val date: LocalDateTime,
        val data: String
    )
}
