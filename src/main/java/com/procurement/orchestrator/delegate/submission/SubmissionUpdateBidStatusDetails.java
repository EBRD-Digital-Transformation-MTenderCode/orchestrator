package com.procurement.orchestrator.delegate.submission;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.SubmissionRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SubmissionUpdateBidStatusDetails implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SubmissionCopyBids.class);

    private final SubmissionRestClient submissionRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public SubmissionUpdateBidStatusDetails(final SubmissionRestClient submissionRestClient,
                                            final OperationService operationService,
                                            final ProcessService processService,
                                            final JsonUtil jsonUtil) {
        this.submissionRestClient = submissionRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();
        final String bidId = processService.getText("bidId", jsonData, processId);
        final String awardStatusDetails = processService.getText("awardStatusDetails", jsonData, processId);
        JsonNode responseData = null;
        if (bidId != null && awardStatusDetails != null) {
            responseData = processService.processResponse(
                    submissionRestClient.updateStatusDetails(
                            context.getCpid(),
                            context.getStage(),
                            bidId,
                            context.getStartDate(),
                            awardStatusDetails),
                    context,
                    processId,
                    taskId,
                    jsonData);
        }
        if (Objects.nonNull(responseData)) {
            operationService.saveOperationStep(
                    execution,
                    entity,
                    jsonData,
                    processService.addUpdatedBid(jsonData, responseData, processId));
        }
    }
}
