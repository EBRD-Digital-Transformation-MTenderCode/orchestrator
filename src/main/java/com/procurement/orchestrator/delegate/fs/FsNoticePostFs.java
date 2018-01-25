package com.procurement.orchestrator.delegate.fs;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import com.procurement.orchestrator.rest.NoticeRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import feign.FeignException;
import java.util.Optional;
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

    private final ProcessService processService;

    private final NoticeRestClient noticeRestClient;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;

    public FsNoticePostFs(final OperationService operationService,
                          final ProcessService processService,
                          final NoticeRestClient noticeRestClient,
                          final JsonUtil jsonUtil,
                          final DateUtil dateUtil) {
        this.operationService = operationService;
        this.processService = processService;
        this.noticeRestClient = noticeRestClient;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
        final Optional<OperationStepEntity> entityOptional = operationService.getPreviousOperationStep(execution);
        if (entityOptional.isPresent()) {
            final OperationStepEntity entity = entityOptional.get();
            final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
            final String cpId = jsonData.get("cpid").asText();
            try {
                final ResponseEntity<ResponseDto> responseEntity = noticeRestClient.createFs(
                        cpId,
                        "fs",
                        jsonData
                );
                operationService.saveOperationStep(
                        execution,
                        entity,
                        responseEntity.getBody().getData());
            } catch (FeignException e) {
                LOG.error(e.getMessage(), e);
                processService.processHttpException(e.status(), e.getMessage(), execution.getProcessInstanceId());
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                processService.processHttpException(0, e.getMessage(), execution.getProcessInstanceId());
            }
        }
    }
}
