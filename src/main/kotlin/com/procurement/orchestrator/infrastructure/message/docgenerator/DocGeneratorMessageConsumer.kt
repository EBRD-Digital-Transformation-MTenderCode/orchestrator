package com.procurement.orchestrator.infrastructure.message.docgenerator

import com.datastax.driver.core.utils.UUIDs
import com.fasterxml.jackson.databind.JsonNode
import com.procurement.orchestrator.application.client.NotificatorClient
import com.procurement.orchestrator.application.model.context.members.Incident
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.ProcessLauncher
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.application.service.events.DocumentGeneratorEvent.DocumentGenerated
import com.procurement.orchestrator.domain.extension.date.format
import com.procurement.orchestrator.domain.extension.date.nowDefaultUTC
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.document.DocumentInitiator
import com.procurement.orchestrator.infrastructure.configuration.property.GlobalProperties
import com.procurement.orchestrator.infrastructure.message.docgenerator.DocumentGeneratorEvent.CONTRACT_FINALIZATION
import com.procurement.orchestrator.infrastructure.message.docgenerator.DocumentGeneratorEvent.DOCUMENT_GENERATED
import com.procurement.orchestrator.service.ProcessService
import com.procurement.orchestrator.service.RequestService
import kotlinx.coroutines.runBlocking
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
    private val incidentNotificatorClient: NotificatorClient<Incident>,
    private val logger: Logger
) {

    @KafkaListener(topics = ["document-generator-out"])
    fun onDocGenerator(message: String, @Header(KafkaHeaders.ACKNOWLEDGMENT) ack: Acknowledgment) {
        try {
            logger.info("Received a message from the Document-Generator ($message).")
            val response: JsonNode = transform.tryParse(message)
                .mapError { Fail.Incident.Bus.Consumer(description = "Cannot parse message", exception = it.exception) }
                .orReturnFail { fail ->
                    handleFail(fail, message, ack)
                    return
                }
            if (response.get("errors") == null) {
                val command = getCommand(response)
                    .orReturnFail { fail ->
                        handleFail(fail, message, ack)
                        return
                    }

                when (command) {
                    CONTRACT_FINALIZATION -> startContractFinalization(response)
                    DOCUMENT_GENERATED -> startAddingGeneratedDocument(response)
                        .doOnFail { fail -> handleFail(fail, message, ack) }
                }

            } else {
                val error = Fail.Incident.Bus.Consumer(description = "Error from document generator.")
                handleFail(error, message, ack)
                return
            }
            ack.acknowledge()
        } catch (e: Exception) {
            val incident = Fail.Incident.Bpe(description = e.message ?: "Error while processing message from document generator", exception = e)
            handleFail(incident, message, ack)
        }
    }

    private fun handleFail(fail: Fail, message: String, ack: Acknowledgment) {
        fail.logging(logger)
        when (fail) {
            is Fail.Incident -> {
                runBlocking {
                    val bpeIncident = createIncident(fail, message)
                    incidentNotificatorClient.send(bpeIncident)
                        .doOnSuccess {
                            if (fail is Fail.Incident.Bus.Consumer)
                                ack.acknowledge()

                            // Used for incident thrown by API V1
                            if (fail is Fail.Incident.Bpe)
                                ack.acknowledge()
                        }
                        .doOnFail { notificationFail -> notificationFail.logging(logger) }
                }
            }
            is Fail.Error -> {
                runBlocking {
                    val bpeIncident = createIncident(fail, message)
                    incidentNotificatorClient.send(bpeIncident)
                        .doOnSuccess { ack.acknowledge() }
                        .doOnFail { notificationFail -> notificationFail.logging(logger) }
                }
            }
        }
    }

    private fun createIncident(fail: Fail, message: String): Incident {
        val level = if (fail is Fail.Incident)
            fail.level
        else
            Fail.Incident.Level.ERROR

        return  Incident(
            id = UUID.randomUUID().toString(),
            date = nowDefaultUTC().format(),
            level = level.key,
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

    private fun startContractFinalization(response: JsonNode) = launchProcessByV1(response, "finalUpdateAC")

    private fun startAddingGeneratedDocument(response: JsonNode): MaybeFail<Fail> {
        val dataNode = response.get("data")
            ?: return MaybeFail.fail(Fail.Incident.Bus.Consumer(description = "Missing 'data' in message."))

        val ocid = dataNode.get("ocid")?.asText()
            ?: return MaybeFail.fail(Fail.Incident.Bus.Consumer(description = "Missing 'ocid' in message."))

        val parsedOcid = Ocid.SingleStage.tryCreateOrNull(ocid)
            ?: return MaybeFail.fail(
                Fail.Incident.Bus.Consumer(
                    description = "Cannot parse 'ocid'. Actual value: '$ocid'. Pattern: '${Ocid.SingleStage.pattern}'"
                )
            )
        val parsedSingleStageOcid = parsedOcid as Ocid.SingleStage

        val cpid = dataNode.get("cpid")?.asText()
            ?: return MaybeFail.fail(Fail.Incident.Bus.Consumer(description = "Missing 'cpid' in message."))

        val parsedCpid = Cpid.tryCreateOrNull(cpid)
            ?: return MaybeFail.fail(
                Fail.Incident.Bus.Consumer(
                    description = "Cannot parse 'cpid'. Actual value: '$cpid'. Pattern: '${Cpid.pattern}'"
                )
            )

        val documentInitiatorNode = dataNode.get("documentInitiator")?.asText()
            ?: return MaybeFail.fail(Fail.Incident.Bus.Consumer(description = "Missing 'documentInitiator' in message."))

        val documentInitiator = DocumentInitiator.orNull(documentInitiatorNode)
            ?: return MaybeFail.fail(Fail.Incident.Bus.Consumer(description = "Unknown value of 'documentInitiator' attribute. Actual value: $documentInitiatorNode"))

        val objectId = dataNode.get("objectId")?.asText()
            ?: return MaybeFail.fail(Fail.Incident.Bus.Consumer(description = "Missing 'objectId' in message."))

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

    fun getCommand(response: JsonNode): Result<DocumentGeneratorEvent, Fail.Incident.Bus.Consumer> {
        val command = response.get("command")?.asText()
            ?: return Result.failure(Fail.Incident.Bus.Consumer(description = "Missing 'command' in message."))
        return DocumentGeneratorEvent.orNull(command)
            ?.let { Result.success(it) }
            ?: return Result.failure(
                Fail.Incident.Bus.Consumer(
                    description = "Unknown value of attribute 'command'. Actual value: '$command'."
                )
            )
    }
}