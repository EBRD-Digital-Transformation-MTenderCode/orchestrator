package com.procurement.orchestrator.delegate.storage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.StorageRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static com.procurement.orchestrator.domain.commands.StorageCommandType.PUBLISH;

@Component
public class StorageOpenDocsOfBusinessFunction implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(StorageOpenDocsOfBusinessFunction.class);

    private final StorageRestClient storageRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public StorageOpenDocsOfBusinessFunction(
        final StorageRestClient storageRestClient,
        final OperationService operationService,
        final ProcessService processService,
        final JsonUtil jsonUtil
    ) {
        this.storageRestClient = storageRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityId());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();

        final JsonNode documents = getDocumentsOfBusinessFunctions(jsonData, processId);
        if (documents != null) {
            final JsonNode commandMessage = processService.getCommandMessage(PUBLISH, context, documents);
            if (LOG.isDebugEnabled())
                LOG.debug("COMMAND ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(commandMessage));

            final ResponseEntity<ResponseDto> response = storageRestClient.execute(commandMessage);
            if (LOG.isDebugEnabled())
                LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));

            final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
            if (LOG.isDebugEnabled())
                LOG.debug("RESPONSE AFTER PROCESSING ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(responseData));

            if (responseData != null) {
                final JsonNode step = setDocumentsOfBusinessFunctions(jsonData, responseData, processId);
                if (LOG.isDebugEnabled())
                    LOG.debug("STEP FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(step));

                operationService.saveOperationStep(execution, entity, commandMessage, step);
            }
        }
    }

    private JsonNode getDocumentsOfBusinessFunctions(final JsonNode jsonData, final String processId) {
        try {
            final JsonNode tenderNode = jsonData.get("tender");

            if (!tenderNode.has("procuringEntity")) return null;
            final JsonNode procuringEntityNode = tenderNode.get("procuringEntity");

            if (!procuringEntityNode.has("persones")) return null;
            final ArrayNode personsArray = (ArrayNode) procuringEntityNode.get("persones");

            final ArrayNode documentsNode = jsonUtil.createArrayNode();
            for (final JsonNode person : personsArray) {
                if (person.has("businessFunctions")) {
                    final ArrayNode businessFunctionsArray = (ArrayNode) person.get("businessFunctions");
                    for (final JsonNode businessFunction : businessFunctionsArray) {
                        if (businessFunction.has("documents")) {
                            final ArrayNode documentsArray = (ArrayNode) businessFunction.get("documents");
                            for (final JsonNode document : documentsArray) {
                                documentsNode.add(document);
                            }
                        }
                    }
                }
            }
            if (documentsNode.size() == 0)
                return null;

            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.set("documents", documentsNode);
            return mainNode;
        } catch (Exception e) {
            LOG.error("Error getting documents of businessFunctions.", e);
            processService.terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    private JsonNode setDocumentsOfBusinessFunctions(final JsonNode jsonData, final JsonNode documentsData, final String processId) {
        try {
            final ObjectNode tenderNode = (ObjectNode) jsonData.get("tender");
            final ArrayNode documentsArray = (ArrayNode) documentsData.get("documents");

            if (!tenderNode.has("procuringEntity")) return null;
            final JsonNode procuringEntityNode = tenderNode.get("procuringEntity");

            if (!tenderNode.has("persones")) return null;
            final ArrayNode personsArray = (ArrayNode) procuringEntityNode.get("persones");

            for (final JsonNode person : personsArray) {
                if (person.has("businessFunctions")) {
                    final ArrayNode businessFunctionsArray = (ArrayNode) person.get("businessFunctions");
                    for (final JsonNode businessFunction : businessFunctionsArray) {
                        if (businessFunction.has("documents")) {
                            final ArrayNode documentsNode = jsonUtil.createArrayNode();
                            final ArrayNode oldDocumentsArray = (ArrayNode) businessFunction.get("documents");
                            for (final JsonNode oldDocument : oldDocumentsArray) {
                                final String oldId = oldDocument.get("id").asText();
                                for (JsonNode newDocument: documentsArray) {
                                    String newId = newDocument.get("id").asText();
                                    if (newId.equals(oldId)) documentsNode.add(newDocument);
                                }
                            }
                            ((ObjectNode) businessFunction).replace("documents", documentsNode);
                        }
                    }
                }
            }
            return jsonData;
        } catch (Exception e) {
            processService.terminateProcess(processId, e.getMessage());
            return null;
        }
    }

}
