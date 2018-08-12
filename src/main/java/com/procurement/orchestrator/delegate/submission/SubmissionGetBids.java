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
public class SubmissionGetBids implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SubmissionGetBids.class);

    private final SubmissionRestClient submissionRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public SubmissionGetBids(final SubmissionRestClient submissionRestClient,
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
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();
        final JsonNode responseData = processService.processResponse(
                submissionRestClient.bidsSelection(
                        context.getCpid(),
                        context.getStage(),
                        context.getCountry(),
                        context.getPmd(),
                        context.getStartDate()),
                context,
                processId,
                taskId,
                jsonUtil.empty());
        if (Objects.nonNull(responseData)) {
            final Boolean isBidsEmpty = processService.isBidsEmpty(responseData, processId);
            if (isBidsEmpty) {
                context.setOperationType("tenderUnsuccessful");
                execution.setVariable("operationType", "tenderUnsuccessful");
            } else {
                final Boolean isPeriodExpired = processService.getBoolean("isPeriodExpired", responseData, processId);
                if (!isPeriodExpired) {
                    execution.setVariable("operationType", "rescheduleEndTenderPeriod");
                    context.setEndDate(processService.getText("tenderPeriodEndDate", responseData, processId));
                }
            }
            operationService.saveOperationStep(
                    execution,
                    entity,
                    context,
                    jsonUtil.empty(),
                    responseData);
        }
    }
}

