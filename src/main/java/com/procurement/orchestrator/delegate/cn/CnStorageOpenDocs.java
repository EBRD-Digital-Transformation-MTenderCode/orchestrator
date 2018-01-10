package com.procurement.orchestrator.delegate.cn;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.cassandra.model.OperationEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.constant.ResponseMessageType;
import com.procurement.orchestrator.rest.StorageRestClient;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CnStorageOpenDocs implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CnStorageOpenDocs.class);

    private final StorageRestClient storageRestClient;

    private final OperationService operationService;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;

    public CnStorageOpenDocs(final StorageRestClient storageRestClient,
                             final OperationService operationService,
                             final JsonUtil jsonUtil,
                             final DateUtil dateUtil) {
        this.storageRestClient = storageRestClient;
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info("->Data preparation for E-Storage.");
        final String txId = execution.getProcessBusinessKey();
        final Optional<OperationEntity> entityOptional = operationService.getLastOperation(txId);
        if (entityOptional.isPresent()) {
            LOG.info("->Send data to E-Storage.");
            final OperationEntity entity = entityOptional.get();
            final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
            final JsonNode tenderNode = jsonData.get("tender");
            final JsonNode tenderPeriodNode = tenderNode.get("tenderPeriod");
            final String startDate = tenderPeriodNode.get("startDate").asText();
            final List<String> fileIds = getFileIds(tenderNode, startDate);
            for (String fileId : fileIds) {
                try {
                    final ResponseEntity<String> responseEntity = storageRestClient.setPublishDate(fileId, startDate);
                    LOG.info("->Get response: " + responseEntity.getBody());
                } catch (Exception e) {
                    LOG.error(e.getMessage());
                    throw new BpmnError("TR_EXCEPTION", ResponseMessageType.SERVICE_EXCEPTION.value());
                }
            }
            JsonNode jsonWithDatePublished = setDatePublished(jsonData, startDate);
            operationService.saveOperation(getEntity(entity, jsonWithDatePublished));
        }
    }

    private List<String> getFileIds(JsonNode tenderNode, String startDate) {
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

    private OperationEntity getEntity(OperationEntity entity, JsonNode jsonData) {
        entity.setJsonData(jsonUtil.toJson(jsonData));
        entity.setDate(dateUtil.getNowUTC());
        return entity;
    }
}
