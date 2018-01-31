package com.procurement.orchestrator.delegate.tender.notice;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.rest.NoticeRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Objects;
import java.util.Optional;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NoticePostCn implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(NoticePostCn.class);

    private final NoticeRestClient noticeRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public NoticePostCn(final NoticeRestClient noticeRestClient,
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
        final Optional<OperationStepEntity> entityOptional = operationService.getPreviousOperationStep(execution);
        if (entityOptional.isPresent()) {
            final OperationStepEntity entity = entityOptional.get();
            final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
            final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
            final String processId = execution.getProcessInstanceId();
            final String operationId = params.getOperationId();
            final String releaseDate = getReleaseDate(jsonData, processId, operationId);
            try {
                final JsonNode responseData = processService.processResponse(
                        noticeRestClient.createCn(params.getCpid(), "ps", releaseDate, jsonData),
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

    private String getReleaseDate(final JsonNode jsonData,
                                  final String processId,
                                  final String operationId) {
        try {
            final JsonNode tenderNode = jsonData.get("tender");
            final JsonNode tenderPeriodNode = tenderNode.get("tenderPeriod");
            return tenderPeriodNode.get("startDate").asText();
        } catch (Exception e) {
            processService.processError(e.getMessage(), processId, operationId);
            return null;
        }
    }
}
