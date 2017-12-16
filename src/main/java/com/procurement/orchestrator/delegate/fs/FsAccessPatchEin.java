package com.procurement.orchestrator.delegate.fs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.cassandra.OperationEntity;
import com.procurement.orchestrator.cassandra.OperationService;
import com.procurement.orchestrator.cassandra.OperationValue;
import com.procurement.orchestrator.domain.constant.ResponseMessageType;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import com.procurement.orchestrator.rest.AccessRestClient;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Optional;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class FsAccessPatchEin implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(FsAccessPatchEin.class);

    private final AccessRestClient accessRestClient;

    private final OperationService operationService;

    private final JsonUtil jsonUtil;

    public FsAccessPatchEin(final AccessRestClient accessRestClient,
                            final OperationService operationService,
                            final JsonUtil jsonUtil) {
        this.accessRestClient = accessRestClient;
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info("->Data preparation for E-Access.");
        final String transactionId = execution.getProcessBusinessKey();
        final Optional<OperationEntity> entityOptional = operationService.getOperationByStep(transactionId, 2);
        if (entityOptional.isPresent()) {
            LOG.info("->Send data to E-Access.");
            final OperationEntity entity = entityOptional.get();
            final ResponseDto response;
            try {
                final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
                final JsonNode requestDto = getJsonForRequest(jsonData);
                final ResponseEntity<ResponseDto> responseEntity = accessRestClient.postAddRelatedProcess(requestDto);
                response = responseEntity.getBody();
                LOG.info("->Get response: " + response.getData());
            } catch (Exception e) {
                LOG.error(e.getMessage());
                throw new BpmnError("TR_EXCEPTION", ResponseMessageType.SERVICE_EXCEPTION.value());
            }

            final OperationValue operation = new OperationValue(
                transactionId,
                4,
                "get EIN release from access",
                "e-access",
                "e-notice",
                entity.getProcessType(),
                jsonUtil.toJson(response.getData()));

            operationService.saveOperation(operation);
        }
    }

    private JsonNode getJsonForRequest(final JsonNode jsonData) {
        final ObjectNode jsonForRequest = jsonUtil.createObjectNode();
        final String cpidKey = "cpid";
        final String ocidKey = "ocid";
        jsonForRequest.put(cpidKey, jsonData.get(cpidKey).asText());
        jsonForRequest.put(ocidKey, jsonData.get(ocidKey).asText());
        return jsonForRequest;
    }
}
