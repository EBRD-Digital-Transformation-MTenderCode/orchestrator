package com.procurement.orchestrator.delegate.tender.submission;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.rest.SubmissionRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Objects;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SubmissionCheckPeriod implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SubmissionCheckPeriod.class);

    private final SubmissionRestClient submissionRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;

    public SubmissionCheckPeriod(final SubmissionRestClient submissionRestClient,
                                 final OperationService operationService,
                                 final ProcessService processService,
                                 final JsonUtil jsonUtil,
                                 final DateUtil dateUtil) {
        this.submissionRestClient = submissionRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
        final String processId = execution.getProcessInstanceId();
        final String operationId = params.getOperationId();
        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        params.setEndDate(getEndDate(jsonData, processId, operationId));
        try {
            final JsonNode responseData = processService.processResponse(
                    submissionRestClient.checkPeriod(
                            params.getCountry(),
                            params.getPmd(),
                            params.getStage(),
                            params.getStartDate(),
                            params.getEndDate()),
                    processId,
                    operationId);
            if (Objects.nonNull(responseData))
                operationService.saveOperationStep(
                        execution,
                        addPeriodStartDate(
                                entity,
                                jsonData,
                                params.getStartDate(),
                                processId,
                                operationId),
                        params);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            processService.processException(e.getMessage(), processId);
        }
    }

    private String getEndDate(final JsonNode jsonData,
                              final String processId,
                              final String operationId) {
        try {
            final JsonNode tenderNode = jsonData.get("tender");
            final JsonNode tenderPeriodNode = tenderNode.get("tenderPeriod");
            return tenderPeriodNode.get("endDate").asText();
        } catch (Exception e) {
            processService.processError(e.getMessage(), processId, operationId);
            return null;
        }
    }

    private OperationStepEntity addPeriodStartDate(final OperationStepEntity entity,
                                                   final JsonNode jsonData,
                                                   final String startDate,
                                                   final String processId,
                                                   final String operationId) {
        try {
            final JsonNode tenderNode = jsonData.get("tender");
            final JsonNode tenderPeriodNode = tenderNode.get("tenderPeriod");
            ((ObjectNode) tenderPeriodNode).put("startDate", startDate);
            entity.setJsonData(jsonUtil.toJson(jsonData));
            return entity;
        } catch (Exception e) {
            processService.processError(e.getMessage(), processId, operationId);
            return null;
        }
    }
}
