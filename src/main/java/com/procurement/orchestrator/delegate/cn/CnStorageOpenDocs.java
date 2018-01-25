package com.procurement.orchestrator.delegate.cn;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.rest.StorageRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import feign.FeignException;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CnStorageOpenDocs implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CnStorageOpenDocs.class);

    private final StorageRestClient storageRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public CnStorageOpenDocs(final StorageRestClient storageRestClient,
                             final OperationService operationService,
                             final ProcessService processService,
                             final JsonUtil jsonUtil) {
        this.storageRestClient = storageRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
        final Optional<OperationStepEntity> entityOptional = operationService.getPreviousOperationStep(execution);
        if (entityOptional.isPresent()) {
            final OperationStepEntity entity = entityOptional.get();
            final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
            final String startDate = getStartDate(jsonData);
            final List<String> fileIds = getFileIds(jsonData);
            try {
                for (String fileId : fileIds) {
                    storageRestClient.setPublishDate(fileId, startDate);
                }
                JsonNode jsonWithDatePublished = setDatePublished(jsonData, startDate);
                operationService.saveOperationStep(execution, entity, jsonWithDatePublished);
            } catch (FeignException e) {
                LOG.error(e.getMessage(), e);
                processService.processHttpException(e.status(), e.getMessage(), execution.getProcessInstanceId());
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private String getStartDate(JsonNode jsonData) {
        final JsonNode tenderNode = jsonData.get("tender");
        final JsonNode tenderPeriodNode = tenderNode.get("tenderPeriod");
        return tenderPeriodNode.get("startDate").asText();
    }

    private List<String> getFileIds(JsonNode jsonData) {
        final JsonNode tenderNode = jsonData.get("tender");
        final List<String> fileIds = new ArrayList();
        final ArrayNode documentsNode = (ArrayNode) tenderNode.get("documents");
        for (final JsonNode fileNode : documentsNode) {
            fileIds.add(fileNode.get("id").asText());
        }
        return fileIds;
    }

    private JsonNode setDatePublished(JsonNode jsonData, String startDate) {
        final JsonNode tenderNode = jsonData.get("tender");
        final ArrayNode documentsNode = (ArrayNode) tenderNode.get("documents");
        for (final JsonNode fileNode : documentsNode) {
            ((ObjectNode) fileNode).put("datePublished", startDate);
        }
        return jsonData;
    }
}
