package com.procurement.orchestrator.delegate.fs;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.rest.NoticeRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Optional;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
            final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
            try {
                final JsonNode responseData = processService.processResponse(
                        noticeRestClient.createFs(
                                params.getCpid(),
                                "fs",
                                jsonData),
                        execution.getProcessInstanceId(),
                        params.getOperationId());
                operationService.saveOperationStep(
                        execution,
                        entity,
                        responseData);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                processService.processException(e.getMessage(), execution.getProcessInstanceId());
            }
        }
    }
}
