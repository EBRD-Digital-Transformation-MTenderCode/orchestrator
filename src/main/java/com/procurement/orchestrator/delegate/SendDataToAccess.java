package com.procurement.orchestrator.delegate;

import com.procurement.orchestrator.cassandra.OperationEntity;
import com.procurement.orchestrator.domain.constant.ResponseMessageType;
import com.procurement.orchestrator.domain.dto.RequestDto;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import com.procurement.orchestrator.rest.AccessRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class SendDataToAccess implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SendDataToAccess.class);

    private final AccessRestClient accessRestClient;

    private final OperationService operationService;

    private final JsonUtil jsonUtil;

    public SendDataToAccess(final AccessRestClient accessRestClient,
                            final OperationService operationService,
                            final JsonUtil jsonUtil) {
        this.accessRestClient = accessRestClient;
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info("->Data preparation for E-Access.");
        String transactionId = execution.getProcessBusinessKey();
        Optional<OperationEntity> entityOptional = operationService.getOperationByStep(transactionId, 1);
        if (entityOptional.isPresent()) {
            OperationEntity entity = entityOptional.get();
            Map<String, String> requestData = new HashMap<>();
            requestData.put("ein", entity.getJsonData());
            RequestDto request = new RequestDto(requestData);
            LOG.info("->Send data to E-Access.");
            ResponseDto response;
            try {
                response = accessRestClient.postData(request).getBody();
                LOG.info("->Get response: " + response.getData().toString());
            } catch (Exception e) {
                LOG.error(e.getMessage());
                throw new BpmnError("TR_EXCEPTION", ResponseMessageType.SERVICE_EXCEPTION.value());
            }
            operationService.saveOperation(
                    transactionId,
                    2,
                    "get from access",
                    "e-access",
                    "e-notice",
                    "ein",
                    jsonUtil.toJson(response.getData()));

        }
    }
}
