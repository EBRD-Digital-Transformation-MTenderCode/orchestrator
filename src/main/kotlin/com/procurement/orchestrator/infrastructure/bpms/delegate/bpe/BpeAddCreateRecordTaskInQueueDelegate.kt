package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe

import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.extension.date.nowDefaultUTC
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractInternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.model.ResultContext
import com.procurement.orchestrator.infrastructure.bpms.model.queue.QueueNoticeTask
import com.procurement.orchestrator.infrastructure.bpms.repository.NoticeQueueRepository
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.springframework.stereotype.Component
import kotlin.coroutines.coroutineContext

@Component
class BpeAddCreateRecordTaskInQueueDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    private val noticeQueueRepository: NoticeQueueRepository,
    private val transform: Transform
) : AbstractInternalDelegate<Unit, Unit>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Option<Unit>, Fail.Incident> {

        val processInfo = context.processInfo
        val cpid = processInfo.cpid
        val ocid = processInfo.ocid
        val data = transform
            .trySerialization(
                QueueNoticeTask.Data(
                    cpid = cpid,
                    ocid = ocid,
                    tender = context.tender,
                    bids = context.bids,
                    awards = context.awards,
                    parties = context.parties,
                    contracts = context.contracts,
                    submissions = context.submissions,
                    qualifications = context.qualifications,
                    preQualification = context.preQualification,
                    invitations = context.invitations,
                    relatedProcesses = context.relatedProcesses
                )
            )
            .orForwardFail { fail -> return fail }

        val requestInfo = context.requestInfo
        noticeQueueRepository
            .save(
                operationId = requestInfo.parentOperationId ?: requestInfo.operationId,
                task = QueueNoticeTask(
                    id = QueueNoticeTask.Id(
                        cpid = cpid,
                        ocid = ocid,
                        action = QueueNoticeTask.Action.CREATE_RECORD
                    ),
                    timestamp = nowDefaultUTC(),
                    data = data
                )
            )
            .doOnError { return failure(it) }

        coroutineContext[ResultContext.Key]!!
            .apply {
                request(data)
            }


        return success(Option.none())
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        data: Unit
    ): MaybeFail<Fail.Incident.Bpmn> = MaybeFail.none()
}
