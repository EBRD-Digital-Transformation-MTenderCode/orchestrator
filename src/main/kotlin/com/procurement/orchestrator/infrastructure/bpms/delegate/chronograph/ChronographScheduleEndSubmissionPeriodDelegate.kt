package com.procurement.orchestrator.infrastructure.bpms.delegate.chronograph

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetPeriod
import com.procurement.orchestrator.application.model.context.extension.tryGetPreQualification
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.delegate.kafka.MessageProducer
import com.procurement.orchestrator.domain.chronograph.ActionType
import com.procurement.orchestrator.domain.chronograph.ScheduleTask
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.message.chronograph.ChronographMessage
import org.springframework.stereotype.Component
import java.util.*

@Component
class ChronographScheduleEndSubmissionPeriodDelegate(
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

        val preQualification = context.tryGetPreQualification()
            .orForwardFail { fail -> return fail }

        val period = preQualification.tryGetPeriod()
            .orForwardFail { fail -> return fail }

        val launchTime = period.endDate!!
        val uuid = UUID.randomUUID().toString()

        val contextChronograph = ChronographMessage.Metadata(
            operationId = uuid,
            requestId = uuid,
            cpid = processInfo.cpid.toString(),
            ocid = processInfo.ocid.toString(),
            processType = PROCESS_TYPE_SUBMISSION_PERIOD_END,
            owner = requestInfo.owner.toString(),
            timestamp = launchTime
        )

        val metaData = transform.trySerialization(contextChronograph)
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
            .doOnFail { fail -> return failure(fail) }

        return success(Reply.None)
    }

    private fun sendToChronograph(task: ScheduleTask): MaybeFail<Fail.Incident.Bus.Producer> = try {
        messageProducer.sendToChronograph(task)
        MaybeFail.none()
    } catch (exception: Exception) {
        MaybeFail.fail(
            Fail.Incident.Bus.Producer(
                description = "Cannot send task to chronograph. task = '${task}'",
                exception = exception
            )
        )
    }
}
