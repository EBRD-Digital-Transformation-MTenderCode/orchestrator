package com.procurement.orchestrator.infrastructure.bpms.delegate.chronograph

import com.datastax.driver.core.utils.UUIDs
import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.delegate.kafka.MessageProducer
import com.procurement.orchestrator.domain.chronograph.ActionType
import com.procurement.orchestrator.domain.chronograph.ScheduleTask
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.model.chronograph.ChronographMetadata
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import org.springframework.stereotype.Component

@Component
class ChronographRescheduleEndTenderPeriodDelegate(
    private val messageProducer: MessageProducer,
    private val transform: Transform,
    operationStepRepository: OperationStepRepository,
    logger: Logger
) : AbstractExternalDelegate<Unit, Unit>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        private const val PROCESS_TYPE_SUBMISSION_PERIOD_END = "submissionPeriodEnd"
        private const val PHASE_SUBMISSION = "submission"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<Unit>, Fail.Incident> {

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo

        val tender = context.tryGetTender()
            .orForwardFail { fail -> return fail }

        val launchTime = tender.tenderPeriod?.endDate
        val uuid = UUIDs.timeBased().toString()

        val contextChronograph = ChronographMetadata(
            operationId = uuid,
            requestId = uuid,
            cpid = processInfo.cpid,
            ocid = processInfo.ocid,
            phase = PHASE_SUBMISSION,
            timeStamp = launchTime,
            processType = PROCESS_TYPE_SUBMISSION_PERIOD_END,
            isAuction = processInfo.isAuction,
            owner = requestInfo.owner,
            platformId = requestInfo.owner
        )

        val metaData = transform.trySerialization(contextChronograph)
            .orForwardFail { fail -> return fail }

        val cancelTask = ScheduleTask(
            ActionType.CANCEL,
            processInfo.cpid.toString(),
            PHASE_SUBMISSION,
            null,
            null,
            null
        )

        sendToChronograph(cancelTask)
            .orForwardFail { fail -> return fail }

        val scheduleTask = ScheduleTask(
            ActionType.SCHEDULE,
            processInfo.cpid.toString(),
            PHASE_SUBMISSION,
            launchTime,
            null,  /*newLaunchTime*/
            metaData
        )

        sendToChronograph(scheduleTask)
            .orForwardFail { fail -> return fail }

        return success(Reply.None)
    }

    private fun sendToChronograph(task: ScheduleTask): Result<Unit, Fail.Incident.Bus.Producer> = try {
        messageProducer.sendToChronograph(task)
        Unit.asSuccess()
    } catch (exception: Exception) {
        failure(
            Fail.Incident.Bus.Producer(
                description = "Cannot send task to chronograph. task = '${task}'",
                exception = exception
            )
        )
    }
}

