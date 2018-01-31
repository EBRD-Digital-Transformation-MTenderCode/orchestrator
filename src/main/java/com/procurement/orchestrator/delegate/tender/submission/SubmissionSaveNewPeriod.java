package com.procurement.orchestrator.delegate.tender.submission;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.rest.SubmissionRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SubmissionSaveNewPeriod implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SubmissionSaveNewPeriod.class);

    private final SubmissionRestClient submissionRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;

    public SubmissionSaveNewPeriod(final SubmissionRestClient submissionRestClient,
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
//        final Optional<OperationStepEntity> entityOptional = operationService.getPreviousOperationStep(execution);
//        if (entityOptional.isPresent()) {
//            final OperationStepEntity entity = entityOptional.get();
//            final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
//            final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
//            final String processId = execution.getProcessInstanceId();
//            final String operationId = params.getOperationId();
//            try {
//                processService.processResponse(
//                        submissionRestClient.savePeriod(
//                                params.getCpid(),
//                                "ps",
//                                getStartDate(jsonData, processId, operationId),
//                                getEndDate(jsonData, processId, operationId)),
//                        processId,
//                        operationId);
//                operationService.saveOperationStep(execution, entity);
//            } catch (Exception e) {
//                LOG.error(e.getMessage(), e);
//                processService.processException(e.getMessage(), processId);
//            }
//        }
    }

    private String getStartDate(final JsonNode jsonData,
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
}
