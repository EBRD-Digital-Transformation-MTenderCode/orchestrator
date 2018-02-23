package com.procurement.orchestrator.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.rest.StorageRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StorageOpenDocs implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(StorageOpenDocs.class);

    private final StorageRestClient storageRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public StorageOpenDocs(final StorageRestClient storageRestClient,
                           final OperationService operationService,
                           final ProcessService processService,
                           final JsonUtil jsonUtil) {
        this.storageRestClient = storageRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityName());
//        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
//        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
//        final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
//        final String processId = execution.getProcessInstanceId();
//        final String operationId = params.getOperationId();
//        final String startDate = params.getStartDate();
//        final JsonNode documents = getDocuments(jsonData, processId, operationId);
//        final String taskId = execution.getCurrentActivityName();
//        JsonNode responseData = processService.processResponse(
//                storageRestClient.setPublishDateBatch(startDate, documents),
//                processId,
//                operationId,
//                taskId);
//        if (Objects.nonNull(responseData))
//            operationService.saveOperationStep(
//                    execution,
//                    entity,
//                    setDatePublished(jsonData, startDate, processId, operationId));
    }

    private JsonNode getDocuments(final JsonNode jsonData,
                                  final String processId,
                                  final String operationId) {
        try {
            final JsonNode tenderNode = jsonData.get("tender");
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            final ArrayNode documentsNode = (ArrayNode) tenderNode.get("documents");
            final ArrayNode documentsArray = mainNode.putArray("documents");
            for (final JsonNode docNode : documentsNode) {
                ObjectNode idNode = jsonUtil.createObjectNode();
                idNode.put("id", docNode.get("id").asText());
                documentsArray.add(idNode);
            }
            return mainNode;
        } catch (Exception e) {
            processService.processError(e.getMessage(), processId, operationId);
            return null;
        }
    }

    private JsonNode setDatePublished(final JsonNode jsonData,
                                      final String startDate,
                                      final String processId,
                                      final String operationId) {
        try {
            final JsonNode tenderNode = jsonData.get("tender");
            final ArrayNode documentsNode = (ArrayNode) tenderNode.get("documents");
            for (final JsonNode fileNode : documentsNode) {
                ((ObjectNode) fileNode).put("datePublished", startDate);
            }
            return jsonData;
        } catch (Exception e) {
            processService.processError(e.getMessage(), processId, operationId);
            return null;
        }
    }
}
