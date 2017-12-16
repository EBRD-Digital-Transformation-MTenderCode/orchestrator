package com.procurement.orchestrator.delegate.fs;

import com.procurement.orchestrator.cassandra.OperationEntity;
import com.procurement.orchestrator.cassandra.OperationService;
import com.procurement.orchestrator.cassandra.OperationValue;
import com.procurement.orchestrator.domain.constant.ResponseMessageType;
import com.procurement.orchestrator.domain.dto.RequestNoticeDto;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import com.procurement.orchestrator.rest.NoticeRestClient;
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
public class FsNoticePostEin implements JavaDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(FsNoticePostEin.class);

    private final OperationService operationService;

    private final NoticeRestClient noticeRestClient;

    private final JsonUtil jsonUtil;

    public FsNoticePostEin(final OperationService operationService,
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
        final Optional<OperationEntity> entityOptional = operationService.getOperationByStep(transactionId, 4);
        if (entityOptional.isPresent()) {
            LOG.info("->Send data to E-Notice.");
            final OperationEntity entity = entityOptional.get();
            final ResponseDto response;
            try {
                final RequestNoticeDto requestDto = jsonUtil.toObject(RequestNoticeDto.class, entity.getJsonData());
                final ResponseEntity<ResponseDto> responseEntity = noticeRestClient.postRelease(requestDto);
                response = responseEntity.getBody();
                LOG.info("->Get response: " + response.getData());
            } catch (Exception e) {
                LOG.error(e.getMessage());
                throw new BpmnError("TR_EXCEPTION", ResponseMessageType.SERVICE_EXCEPTION.value());
            }
            final OperationValue operation = new OperationValue(
                transactionId,
                5,
                "get confirmation from e-notice",
                "e-notice",
                "platform",
                entity.getProcessType(),
                jsonUtil.toJson(response.getData()));

            operationService.saveOperation(operation);
        }
    }
}
