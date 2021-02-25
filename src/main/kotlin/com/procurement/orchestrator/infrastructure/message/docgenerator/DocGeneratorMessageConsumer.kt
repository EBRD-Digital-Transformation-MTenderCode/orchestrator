package com.procurement.orchestrator.infrastructure.message.docgenerator

import com.datastax.driver.core.utils.UUIDs
import com.fasterxml.jackson.databind.JsonNode
import com.procurement.orchestrator.application.service.ProcessLauncher
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.application.service.events.DocumentGeneratorEvent.DocumentGenerated
import com.procurement.orchestrator.domain.commands.DocGeneratorCommandType
import com.procurement.orchestrator.domain.commands.DocGeneratorCommandType.CONTRACT_FINALIZATION
import com.procurement.orchestrator.domain.commands.DocGeneratorCommandType.DOCUMENT_GENERATED
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.document.DocumentInitiator
import com.procurement.orchestrator.exceptions.ErrorException
import com.procurement.orchestrator.exceptions.ErrorType
import com.procurement.orchestrator.service.ProcessService
import com.procurement.orchestrator.service.RequestService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header

class DocGeneratorMessageConsumer(
    private val processService: ProcessService,
    private val requestService: RequestService,
    private val transform: Transform,
    private val processLauncher: ProcessLauncher
) {
    companion object {
        private val LOG = LoggerFactory.getLogger(DocGeneratorMessageConsumer::class.java)
    }

    @KafkaListener(topics = ["document-generator-out"])
    fun onDocGenerator(message: String, @Header(KafkaHeaders.ACKNOWLEDGMENT) ack: Acknowledgment) {
        try {
            LOG.info("Received a message from the Document-Generator ($message).")
            val response: JsonNode = transform.tryToJsonNode(message).orThrow { it.exception }
            if (response.get("errors") == null) {
                val command = response.get("command").asText()
                when (DocGeneratorCommandType.fromValue(command)) {
                    CONTRACT_FINALIZATION -> startContractFinalication(response)
                    DOCUMENT_GENERATED -> startAddingGeneratedDocument(response)

                    else -> {
                        val uuid: String = UUIDs.timeBased().toString()
                        requestService.saveRequest(uuid, uuid, null, response.get("data"))
                    }
                }
            } else {
                val uuid: String = UUIDs.timeBased().toString()
                requestService.saveRequest(uuid, uuid, null, response.get("errors"))
            }
            ack.acknowledge()
        } catch (e: Exception) {
            //TODO error processing
            LOG.error("Error while processing the message from the Document-Generator ($message).", e)
        }
    }


    private fun startContractFinalication(response: JsonNode) = launchProcessByV1(response, "finalUpdateAC")

    private fun startAddingGeneratedDocument(response: JsonNode) {
        val dataNode = response.get("data")
        val ocid = dataNode.get("ocid").asText()
        val parsedOcid = Ocid.SingleStage.tryCreateOrNull(ocid)
            ?: throw ErrorException(
                error = ErrorType.INVALID_ATTRIBUTE_VALUE,
                message = "Invalid ocid"
            )
        val parsedSingleStageOcid = parsedOcid as Ocid.SingleStage

        val parsedCpid = Cpid.tryCreateOrNull(dataNode.get("cpid").asText())
            ?: throw ErrorException(
                error = ErrorType.INVALID_ATTRIBUTE_VALUE,
                message = "Invalid cpid"
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

        processLauncher.launch(event)
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