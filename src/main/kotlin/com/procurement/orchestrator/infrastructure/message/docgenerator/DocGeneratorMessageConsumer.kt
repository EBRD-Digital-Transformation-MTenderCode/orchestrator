package com.procurement.orchestrator.infrastructure.message.docgenerator

import com.datastax.driver.core.utils.UUIDs
import com.fasterxml.jackson.databind.JsonNode
import com.procurement.orchestrator.application.client.NotificatorClient
import com.procurement.orchestrator.application.model.context.members.Incident
import com.procurement.orchestrator.application.service.ProcessLauncher
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.application.service.events.DocumentGeneratorEvent.DocumentGenerated
import com.procurement.orchestrator.domain.commands.DocGeneratorCommandType
import com.procurement.orchestrator.domain.commands.DocGeneratorCommandType.CONTRACT_FINALIZATION
import com.procurement.orchestrator.domain.commands.DocGeneratorCommandType.DOCUMENT_GENERATED
import com.procurement.orchestrator.domain.extension.date.format
import com.procurement.orchestrator.domain.extension.date.nowDefaultUTC
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.document.DocumentInitiator
import com.procurement.orchestrator.infrastructure.configuration.property.GlobalProperties
import com.procurement.orchestrator.service.ProcessService
import com.procurement.orchestrator.service.RequestService
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import java.util.*
import kotlin.collections.LinkedHashMap

class DocGeneratorMessageConsumer(
    private val processService: ProcessService,
    private val requestService: RequestService,
    private val transform: Transform,
    private val processLauncher: ProcessLauncher,
    private val incidentNotificatorClient: NotificatorClient<Incident>
) {
    companion object {
        private val LOG = LoggerFactory.getLogger(DocGeneratorMessageConsumer::class.java)
    }

    @KafkaListener(topics = ["document-generator-out"])
    fun onDocGenerator(message: String, @Header(KafkaHeaders.ACKNOWLEDGMENT) ack: Acknowledgment) {
        try {
            LOG.info("Received a message from the Document-Generator ($message).")
            val response: JsonNode = transform.tryParse(message)
                .mapError { Fail.Incident.Bus.Consumer(description = "Cannot parse message", exception = it.exception) }
                .orReturnFail { fail ->
                    handleFail(fail, message, ack)
                    return
                }
            if (response.get("errors") == null) {
                val command = response.get("command").asText()
                when (DocGeneratorCommandType.fromValue(command)) {
                    CONTRACT_FINALIZATION -> startContractFinalication(response)
                    DOCUMENT_GENERATED -> startAddingGeneratedDocument(response).doOnFail { fail -> handleFail(fail, message, ack) }

                    else -> {
                        val fail = Fail.Incident.Bus.Consumer(description = "Unknown command from document generator.")
                        handleFail(fail, message, ack)
                        return
                    }
                }
            } else {
                val error = Fail.Incident.Bus.Consumer(description = "Error from document generator.")
                handleFail(error, message, ack)
                return
            }
            ack.acknowledge()
        } catch (e: Exception) {
            //TODO error processing
            LOG.error("Error while processing the message from the Document-Generator ($message).", e)
        }
    }

    private fun handleFail(fail: Fail, message: String, ack: Acknowledgment) {
        val exception = fail.exception ?: RuntimeException(fail.message)
        when (fail) {
            is Fail.Incident -> {
                LOG.error("Error while processing the message from the Document-Generator ($message).", exception)
                runBlocking {
                    val bpeIncident = createIncident(fail, message)
                    incidentNotificatorClient.send(bpeIncident).doOnFail { notificationFail ->
                        LOG.error("Error while sending message to incident notifier ($notificationFail).", exception)
                    }
                }

                if (fail is Fail.Incident.Bus.Consumer)
                    ack.acknowledge()
            }
            is Fail.Error -> {
                LOG.error("Error while processing the message from the Document-Generator ($message).", exception)
                runBlocking {
                    val bpeIncident = createIncident(fail, message)
                    incidentNotificatorClient.send(bpeIncident).doOnFail { notificationFail ->
                        LOG.error("Error while sending message to incident notifier ($notificationFail).", exception)
                    }
                }
                ack.acknowledge()
            }
        }
    }

    private fun createIncident(fail: Fail, message: String): Incident {
        val level = if (fail is Fail.Incident)
            fail.level.key
        else
            Fail.Incident.Level.ERROR.key

        return  Incident(
            id = UUID.randomUUID().toString(),
            date = nowDefaultUTC().format(),
            level = level,
            service = GlobalProperties.service
                .let { service ->
                    Incident.Service(
                        id = service.id,
                        name = service.name,
                        version = service.version
                    )
                },
            details = listOf(
                Incident.Detail(
                    code = fail.code,
                    description = fail.description,
                    metadata = message
                )
            )
        )
    }

    private fun startContractFinalication(response: JsonNode) = launchProcessByV1(response, "finalUpdateAC")

    private fun startAddingGeneratedDocument(response: JsonNode): MaybeFail<Fail> {
        val dataNode = response.get("data")
        val ocid = dataNode.get("ocid").asText()
        val parsedOcid = Ocid.SingleStage.tryCreateOrNull(ocid)
            ?: return MaybeFail.fail(
                Fail.Incident.Bus.Consumer(
                    description = "Cannot parse 'ocid'. Actual value: '$ocid'. Pattern: '${Ocid.SingleStage.pattern}'"
                )
            )
        val parsedSingleStageOcid = parsedOcid as Ocid.SingleStage

        val cpid = dataNode.get("cpid").asText()
        val parsedCpid = Cpid.tryCreateOrNull(cpid)
            ?: return MaybeFail.fail(
                Fail.Incident.Bus.Consumer(
                    description = "Cannot parse 'cpid'. Actual value: '$cpid'. Pattern: '${Cpid.pattern}'"
                )
            )

        val documentInitiator = DocumentInitiator.creator(dataNode.get("documentInitiator").asText())
        val objectId = dataNode.get("objectId").asText()

        val event = DocumentGenerated(
            cpid = parsedCpid,
            ocid = parsedSingleStageOcid,
            documentInitiator = documentInitiator,
            processName = "addGeneratedDocument",
            objectId = objectId,
            data = dataNode
        )

        return processLauncher.launch(event)
    }

    private fun launchProcessByV1(response: JsonNode, processType: String) {
        val dataNode = response.get("data")
        if (dataNode != null) {
            val ocid = dataNode.get("ocid").asText()
            val prevContext = requestService.getContext(ocid)
            val uuid = UUIDs.timeBased().toString()
            val context = requestService.checkRulesAndProcessContext(prevContext, processType, uuid)
            requestService.saveRequestAndCheckOperation(context, dataNode)
            val variables: MutableMap<String, Any> = LinkedHashMap()
            variables["operationType"] = context.operationType
            processService.startProcess(context, variables)
        }
    }
}