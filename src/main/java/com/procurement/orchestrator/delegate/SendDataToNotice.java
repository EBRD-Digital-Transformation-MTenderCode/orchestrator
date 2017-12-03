package com.procurement.orchestrator.delegate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.procurement.orchestrator.cassandra.OperationEntity;
import com.procurement.orchestrator.domain.constant.ResponseMessageType;
import com.procurement.orchestrator.domain.dto.RequestDto;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import com.procurement.orchestrator.rest.NoticeRestClient;
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
public class SendDataToNotice implements JavaDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(SendDataToNotice.class);

    private final OperationService operationService;

    private final NoticeRestClient noticeRestClient;

    private final JsonUtil jsonUtil;

    public SendDataToNotice(final OperationService operationService,
                            final NoticeRestClient noticeRestClient,
                            final JsonUtil jsonUtil) {
        this.operationService = operationService;
        this.noticeRestClient = noticeRestClient;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info("->Data preparation for E-Notice.");
        String transactionId = execution.getProcessBusinessKey();
        Optional<OperationEntity> entityOptional = operationService.getOperationByStep(transactionId, 2);
        if (entityOptional.isPresent()) {
            OperationEntity entity = entityOptional.get();
            HashMap<String,String> jsonData = jsonUtil.toObject(HashMap.class, entity.getJsonData());
            String osid = jsonData.get("ocid");
            Map<String, String> requestData = new HashMap<>();
            requestData.put("ein",  jsonData.get("ein"));
            RequestDto request = new RequestDto(requestData);
            LOG.info("->Send data to E-Notice.");
            ResponseDto response;
            try {
                response = noticeRestClient.postData(osid, request).getBody();
                LOG.info("->Get response: " + response.getData().toString());
            } catch (Exception e) {
                LOG.error(e.getMessage());
                throw new BpmnError("TR_EXCEPTION", ResponseMessageType.SERVICE_EXCEPTION.value());
            }
            operationService.saveOperation(
                    transactionId,
                    3,
                    "get from e-notice",
                    "e-notice",
                    "platform",
                    "ein",
                    jsonUtil.toJson(response.getData()));

        }
    }
}
