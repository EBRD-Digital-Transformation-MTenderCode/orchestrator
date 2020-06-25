package com.procurement.orchestrator.infrastructure.message.chronograph

import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.PlatformId
import com.procurement.orchestrator.application.model.RequestId
import com.procurement.orchestrator.application.service.ChronographEvent
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.ProcessLauncher
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.Context
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.infrastructure.web.controller.parseCpid
import com.procurement.orchestrator.infrastructure.web.controller.parseSingleStageOcid
import com.procurement.orchestrator.service.ProcessService
import com.procurement.orchestrator.service.RequestService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import java.time.LocalDateTime

class ChronographMessageConsumer(
    private val processService: ProcessService,
    private val requestService: RequestService,
    private val transform: Transform,
    private val processLauncher: ProcessLauncher,
    private val logger: Logger
) {
    companion object {
        private val LOG = LoggerFactory.getLogger(ChronographMessageConsumer::class.java)
    }

    @KafkaListener(topics = ["chronograph-out"])
    fun onChronograph(
        message: String,
        @Header(KafkaHeaders.ACKNOWLEDGMENT) acknowledgment: Acknowledgment
    ) {
        try {
            LOG.info("Received a message from the Chronograph ($message).")
            val response = transform.tryDeserialization(message, ChronographMessage::class.java)
                .orThrow { it.exception }

            if ("NOTIFICATION" == response.status.toUpperCase()) {
                val result = if ("SUBMISSION" == response.data.phase.toUpperCase())
                    processingNew(response.data)
                else
                    processingOld(response.data)
                result.doOnFail { error -> error.logging(logger) }
            }
            acknowledgment.acknowledge()
        } catch (e: Exception) {
            LOG.error("Error while processing the message from the Chronograph ($message).", e)
        }
    }

    private fun processingOld(data: ChronographMessage.Data): MaybeFail<Fail> {
        val contextChronograph = transform.tryDeserialization(data.metadata, Context::class.java)
            .orReturnFail { return MaybeFail.fail(it) }

        val prevContext: Context = requestService.getContext(contextChronograph.cpid)
        val context = requestService.checkRulesAndProcessContext(
            prevContext,
            contextChronograph.processType,
            contextChronograph.requestId
        )
        requestService.saveRequestAndCheckOperation(context, null)
        val variables: MutableMap<String, Any> = mutableMapOf(
            "operationType" to context.operationType
        )
        processService.startProcess(context, variables)

        return MaybeFail.none()
    }

    private fun processingNew(data: ChronographMessage.Data): MaybeFail<Fail> {
        val payload = transform.tryDeserialization(data.metadata, ChronographMessage.Metadata::class.java)
            .orReturnFail { return MaybeFail.fail(it) }
        val cpid: Cpid = parseCpid(payload.cpid)
            .orReturnFail { return MaybeFail.fail(it) }
        val ocid: Ocid = parseSingleStageOcid(payload.ocid)
            .orReturnFail { return MaybeFail.fail(it) }
        val operationId: OperationId = OperationId.tryCreateOrNull(payload.operationId)!!
        val requestId: RequestId = RequestId.tryCreateOrNull(payload.requestId)!!
        val owner: Owner = Owner.tryCreateOrNull(payload.owner)!!
        val platformId: PlatformId = owner
        val timestamp: LocalDateTime = payload.timestamp

        val event = ChronographEvent(
            operationId = operationId,
            platformId = platformId,
            cpid = cpid,
            ocid = ocid,
            owner = owner,
            timestamp = timestamp,
            processName = payload.processType,
            requestId = requestId
        )
        return processLauncher.launch(event)
    }
}