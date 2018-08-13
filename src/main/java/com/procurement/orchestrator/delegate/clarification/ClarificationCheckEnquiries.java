package com.procurement.orchestrator.delegate.clarification;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.ClarificationRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Objects;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ClarificationCheckEnquiries implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ClarificationCheckEnquiries.class);

    private final ClarificationRestClient clarificationRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public ClarificationCheckEnquiries(final ClarificationRestClient clarificationRestClient,
                                       final OperationService operationService,
                                       final ProcessService processService,
                                       final JsonUtil jsonUtil) {
        this.clarificationRestClient = clarificationRestClient;
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
                clarificationRestClient.checkEnquiries(
                        context.getCpid(),
                        context.getStage(),
                        context.getStartDate()),
                context,
                processId,
                taskId,
                jsonUtil.empty());
        if (Objects.nonNull(responseData)) {
            processContext(execution, context, responseData, processId);
            operationService.saveOperationStep(execution, entity, context, jsonUtil.empty(), responseData);
        }
    }

    private void processContext(final DelegateExecution execution, final Context context, final JsonNode responseData, final String processId) {
        final Boolean isTenderPeriodExpired = processService.getBoolean("isTenderPeriodExpired", responseData, processId);
        if (!isTenderPeriodExpired) {
            execution.setVariable("operationType", "rescheduleEndTenderPeriod");
            context.setOperationType("rescheduleEndTenderPeriod");
            context.setEndDate(processService.getText("tenderPeriodEndDate", responseData, processId));
        } else {
            final Boolean allAnswered = processService.getBoolean("allAnswered", responseData, processId);
            if (!allAnswered) {
                execution.setVariable("operationType", "suspendTender");
                context.setOperationType("suspendTender");
            }
        }
    }
}
