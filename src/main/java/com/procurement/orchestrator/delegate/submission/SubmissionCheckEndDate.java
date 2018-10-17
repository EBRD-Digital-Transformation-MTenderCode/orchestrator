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

import static com.procurement.orchestrator.domain.commands.SubmissionCommandType.CHECK_PERIOD_END_DATE;

@Component
public class SubmissionCheckEndDate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SubmissionCheckEndDate.class);

    private final SubmissionRestClient submissionRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public SubmissionCheckEndDate(final SubmissionRestClient submissionRestClient,
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
        final JsonNode commandMessage = processService.getCommandMessage(CHECK_PERIOD_END_DATE, context, jsonUtil.empty());
        JsonNode responseData = processService.processResponse(
                submissionRestClient.execute(commandMessage),
                context,
                processId,
                taskId,
                commandMessage);
        if (Objects.nonNull(responseData)) {
            final Boolean isTenderPeriodExpired = processService.getBoolean("isTenderPeriodExpired", responseData, processId);
            execution.setVariable("isTenderPeriodExpired", isTenderPeriodExpired);
            operationService.saveOperationStep(
                    execution,
                    entity,
                    commandMessage,
                    processService.addTenderTenderPeriod(jsonUtil.empty(), responseData, processId));
        }
    }
}