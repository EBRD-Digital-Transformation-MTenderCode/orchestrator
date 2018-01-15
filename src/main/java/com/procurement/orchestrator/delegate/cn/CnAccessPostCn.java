package com.procurement.orchestrator.delegate.cn;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.domain.constant.ResponseMessageType;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import com.procurement.orchestrator.rest.AccessRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Optional;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CnAccessPostCn implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CnAccessPostCn.class);

    private final AccessRestClient accessRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public CnAccessPostCn(final AccessRestClient accessRestClient,
                          final OperationService operationService,
                          final ProcessService processService,
                          final JsonUtil jsonUtil) {
        this.accessRestClient = accessRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info("->Data preparation for E-Access.");
        final String txId = execution.getProcessBusinessKey();
        final Optional<OperationEntity> entityOptional = operationService.getLastOperation(txId);
        if (entityOptional.isPresent()) {
            LOG.info("->Send data to E-Access.");
            final OperationEntity entity = entityOptional.get();
            final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
            HttpStatus httpStatus = null;
            try {
                final ResponseEntity<ResponseDto> responseEntity = accessRestClient.postCreateCn(
                        params.getCountry(),
                        params.getPmd(),
                        "ps",
                        params.getOwner(),
                        jsonUtil.toJsonNode(entity.getJsonData()));
                httpStatus = responseEntity.getStatusCode();
                operationService.processResponse(entity, params, responseEntity.getBody().getData());
            } catch (Exception e) {
                LOG.error(e.getMessage());
                processService.processHttpException(httpStatus.is4xxClientError(), e.getMessage(),
                        execution.getProcessInstanceId());
            }
        }
    }
}
