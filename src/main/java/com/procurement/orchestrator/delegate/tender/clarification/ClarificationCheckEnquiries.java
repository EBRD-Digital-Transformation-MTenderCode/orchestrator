package com.procurement.orchestrator.delegate.tender.clarification;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.model.Params;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.rest.ClarificationRestClient;
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
        final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
        final String processId = execution.getProcessInstanceId();
        final String operationId = params.getOperationId();
        final String taskId = execution.getCurrentActivityId();
        final JsonNode responseData = processService.processResponse(
                clarificationRestClient.checkEnquiries(params.getCpid(), params.getStage()),
                processId,
                operationId,
                taskId);
        if (Objects.nonNull(responseData)) {
            final Boolean allAnswered = getAllAnswered(responseData, processId);
            if (allAnswered != null) {
                execution.setVariable("checkEnquiries", (allAnswered ? 1 : 2));
                params.setOperationType("tenderPeriodEnd");
            } else {
                final String endDate = getTenderPeriodEndDate(responseData, processId);
                execution.setVariable("checkEnquiries", 3);
                if (endDate != null) {
                    params.setEndDate(endDate);
                    params.setOperationType("updateTenderStatusDetails");
                }
            }
            operationService.saveOperationStep(execution, entity, params, responseData);
        }
    }

    private Boolean getAllAnswered(final JsonNode responseData, final String processId) {
        return processService.getBoolean("allAnswered", responseData, processId);
    }

    private String getTenderPeriodEndDate(final JsonNode responseData, final String processId) {
        return processService.getText("tenderPeriodEndDate", responseData, processId);
    }
}
