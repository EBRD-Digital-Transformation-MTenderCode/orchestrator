package com.procurement.orchestrator.delegate.fs;

import com.procurement.orchestrator.cassandra.OperationEntity;
import com.procurement.orchestrator.cassandra.OperationValue;
import com.procurement.orchestrator.domain.constant.ResponseMessageType;
import com.procurement.orchestrator.domain.dto.RequestDto;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import com.procurement.orchestrator.rest.NoticeRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class FsNoticePostFs implements JavaDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(FsNoticePostFs.class);

    private final OperationService operationService;

    private final NoticeRestClient noticeRestClient;

    private final JsonUtil jsonUtil;

    public FsNoticePostFs(final OperationService operationService,
                          final NoticeRestClient noticeRestClient,
                          final JsonUtil jsonUtil) {
        this.operationService = operationService;
        this.noticeRestClient = noticeRestClient;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info("->Data preparation for E-Notice.");
        final String transactionId = execution.getProcessBusinessKey();
        final Optional<OperationEntity> entityOptional = operationService.getOperationByStep(transactionId, 2);
        if (entityOptional.isPresent()) {
            LOG.info("->Send data to E-Notice.");
            /**getting json data from the entity*/
            final OperationEntity entity = entityOptional.get();
            final Map<String, Object> jsonData = jsonUtil.toObject(LinkedHashMap.class, entity.getJsonData());
            final String cpId = getCpId(jsonData);
            /**preparation data for the request*/
            final RequestDto request = new RequestDto(jsonData);
            final ResponseDto response;
            try {
                final ResponseEntity<ResponseDto> responseEntity = noticeRestClient.postRelease(cpId, request);
                response = responseEntity.getBody();
                LOG.info("->Get response: " + response.getData());
            } catch (Exception e) {
                LOG.error(e.getMessage());
                throw new BpmnError("TR_EXCEPTION", ResponseMessageType.SERVICE_EXCEPTION.value());
            }
            final OperationValue operation = new OperationValue(
                transactionId,
                3,
                "get from e-notice",
                "e-notice",
                "platform",
                entity.getProcessType(),
                jsonUtil.toJson(response.getData()));

            operationService.saveOperation(operation);
        }
    }

    private String getCpId(Map<String, Object> jsonData) {
        final List relatedProcesses = (List) jsonData.get("relatedProcesses");
        if (!relatedProcesses.isEmpty()) {
            final Map relatedProcess = (HashMap) relatedProcesses.get(0);
            final String cpId = (String) relatedProcess.get("ocid");
            return cpId;
        }
        return "";
    }
}
