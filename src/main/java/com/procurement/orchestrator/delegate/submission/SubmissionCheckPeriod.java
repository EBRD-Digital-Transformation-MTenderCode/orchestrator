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
public class SubmissionCheckPeriod implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SubmissionCheckPeriod.class);

    private final SubmissionRestClient submissionRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public SubmissionCheckPeriod(final SubmissionRestClient submissionRestClient,
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
                submissionRestClient.checkPeriod(
                        context.getCpid(),
                        context.getStage(),
                        context.getCountry(),
                        context.getPmd(),
                        context.getOperationType(),
                        context.getStartDate(),
                        context.getEndDate()),
                context,
                processId,
                taskId,
                jsonUtil.empty());
        if (Objects.nonNull(responseData)) {
            execution.setVariable("isPeriodChanged", processService.getBoolean("isPeriodChanged", responseData, processId));
            context.setSetExtendedPeriod(processService.getBoolean("setExtendedPeriod", responseData, processId));
            context.setEndDate(processService.getText("tenderPeriodEndDate", responseData, processId));
            operationService.saveOperationStep(execution, entity, context);
        }
    }
}
