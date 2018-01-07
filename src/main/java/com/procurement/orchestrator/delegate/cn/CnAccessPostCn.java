package com.procurement.orchestrator.delegate.cn;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.constant.ResponseMessageType;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import com.procurement.orchestrator.rest.AccessRestClient;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CnAccessPostCn implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CnAccessPostCn.class);

    private final AccessRestClient accessRestClient;

    private final OperationService operationService;

    private final JsonUtil jsonUtil;

    public CnAccessPostCn(final AccessRestClient accessRestClient,
                          final OperationService operationService,
                          final JsonUtil jsonUtil) {
        this.accessRestClient = accessRestClient;
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info("->Data preparation for E-Access.");
//        final String transactionId = execution.getProcessBusinessKey();
//        final Optional<OperationEntity> entityOptional = operationService.getOperationByStep(transactionId, 1);
//        if (entityOptional.isPresent()) {
//            LOG.info("->Send data to E-Access.");
//            final OperationEntity entity = entityOptional.get();
//            final ResponseDto response;
//            try {
//                final JsonNode requestDto = getJsonForRequest(entity);
//                final ResponseEntity<ResponseDto> responseEntity = accessRestClient.postCreateCn(requestDto);
//                response = responseEntity.getBody();
//                LOG.info("->Get response: " + response.getData());
//            } catch (Exception e) {
//                LOG.error(e.getMessage());
//                throw new BpmnError("TR_EXCEPTION", ResponseMessageType.SERVICE_EXCEPTION.value());
//            }
//
//            final OperationValue operation = new OperationValue(
//                    transactionId,
//                    2,
//                    "get MS release from access",
//                    "e-access",
//                    "e-notice",
//                    entity.getProcessType(),
//                    jsonUtil.toJson(response.getData()));
//
//            operationService.saveOperation(operation);
//        }
    }

    private JsonNode getJsonForRequest(final OperationEntity entity) {
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
        final JsonNode tenderNode = jsonData.get("tender");
        return tenderNode;
    }
}
