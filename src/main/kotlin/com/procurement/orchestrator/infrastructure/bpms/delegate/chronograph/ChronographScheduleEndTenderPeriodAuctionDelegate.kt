package com.procurement.orchestrator.infrastructure.bpms.delegate.chronograph

import com.datastax.driver.core.utils.UUIDs
import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.delegate.kafka.MessageProducer
import com.procurement.orchestrator.domain.Context
import com.procurement.orchestrator.domain.chronograph.ActionType
import com.procurement.orchestrator.domain.chronograph.ScheduleTask
import com.procurement.orchestrator.domain.extension.date.toMilliseconds
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import org.springframework.stereotype.Component

@Component
class ChronographScheduleEndTenderPeriodAuctionDelegate(
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
        private const val PROCESS_TYPE_TENDER_PERIOD_END_AUCTION = "tenderPeriodEndAuction"
        private const val PHASE_TENDERING = "tendering"
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

        val launchTime = tender.tenderPeriod!!.endDate!!
        val uuid = UUIDs.timeBased().toString()

        val contextChronograph = Context.Builder()
            .setOperationId(uuid)
            .setRequestId(uuid)
            .setCpid(processInfo.cpid.toString())
            .setOcid(processInfo.ocid.toString())
            .setPhase(PHASE_TENDERING)
            .setTimeStamp(launchTime.toMilliseconds())
            .setProcessType(PROCESS_TYPE_TENDER_PERIOD_END_AUCTION)
            .setIsAuction(processInfo.isAuction)
            .setOwner(requestInfo.owner.toString())
            .build()

        val metaData = transform.trySerialization(contextChronograph)
            .orForwardFail { fail -> return fail }

        val task = ScheduleTask(
            ActionType.SCHEDULE,
            processInfo.cpid.toString(),
            PHASE_TENDERING,
            launchTime,
            null,  /*newLaunchTime*/
            metaData
        )

        sendToChronograph(task)
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
