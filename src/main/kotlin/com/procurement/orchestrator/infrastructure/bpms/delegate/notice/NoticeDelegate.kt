package com.procurement.orchestrator.infrastructure.bpms.delegate.notice

import com.procurement.orchestrator.application.client.NoticeClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.NoticeQueueRepository
import com.procurement.orchestrator.infrastructure.bpms.repository.NoticeTask
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
    transform: Transform
) : AbstractExternalDelegate<Unit, Unit>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(context: CamundaGlobalContext, parameters: Unit): Result<Reply<Unit>, Fail.Incident> {

        val requestInfo = context.requestInfo
        val operationId = requestInfo.operationId

        val tasks: List<NoticeTask> = noticeQueueRepository.load(operationId = operationId)
            .doOnError { return failure(it) }
            .get

        tasks.forEach { task ->
            when (task.action) {
                NoticeTask.Action.UPDATE_RECORD -> {
                    val params = UpdateRecordAction.Params(startDate = requestInfo.timestamp, data = task.data)
                    val reply = noticeClient.updateRecord(params)
                        .doOnError { return failure(it) }
                        .get

                    when (reply.result) {
                        is Reply.Result.Success -> Unit
                        is Reply.Result.Errors -> return success(reply)
                        is Reply.Result.Incident -> return success(reply)
                    }
                }
                NoticeTask.Action.CREATE_RECORD -> {
                    val params = CreateRecordAction.Params(startDate = requestInfo.timestamp, data = task.data)
                    val reply = noticeClient.createRecord(params)
                        .doOnError { return failure(it) }
                        .get

                    when (reply.result) {
                        is Reply.Result.Success -> Unit
                        is Reply.Result.Errors -> return success(reply)
                        is Reply.Result.Incident -> return success(reply)
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
