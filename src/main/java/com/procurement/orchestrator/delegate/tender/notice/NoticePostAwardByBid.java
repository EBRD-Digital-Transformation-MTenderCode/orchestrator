package com.procurement.orchestrator.delegate.tender.notice;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.rest.NoticeRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Objects;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NoticePostAwardByBid implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(NoticePostAwardByBid.class);

    private final NoticeRestClient noticeRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public NoticePostAwardByBid(final NoticeRestClient noticeRestClient,
                                final OperationService operationService,
                                final ProcessService processService,
                                final JsonUtil jsonUtil) {
        this.noticeRestClient = noticeRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
        final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
        final String processId = execution.getProcessInstanceId();
        final String operationId = params.getOperationId();
        try {
            final JsonNode responseData = processService.processResponse(
                    noticeRestClient.createAwards(
                            params.getCpid(),
                            params.getOcid(),
                            params.getStage(),
                            jsonData),
                    processId,
                    operationId);
            if (Objects.nonNull(responseData))
                operationService.saveOperationStep(execution, entity, responseData);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            processService.processException(e.getMessage(), processId);
        }
    }
}
