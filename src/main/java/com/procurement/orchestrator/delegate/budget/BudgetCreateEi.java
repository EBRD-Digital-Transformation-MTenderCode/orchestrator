package com.procurement.orchestrator.delegate.budget;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.BudgetRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static com.procurement.orchestrator.domain.commands.BudgetCommandType.CREATE_EI;

@Component
public class BudgetCreateEi implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(BudgetCreateEi.class);

    private final BudgetRestClient budgetRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public BudgetCreateEi(
        final BudgetRestClient budgetRestClient,
        final OperationService operationService,
        final ProcessService processService,
        final JsonUtil jsonUtil
    ) {
        this.budgetRestClient = budgetRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityId());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final JsonNode requestData = jsonUtil.toJsonNode(entity.getResponseData());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();

        final JsonNode commandMessage = processService.getCommandMessage(CREATE_EI, context, requestData);
        if (LOG.isDebugEnabled())
            LOG.debug("COMMAND ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(commandMessage));

        final ResponseEntity<ResponseDto> response = budgetRestClient.execute(commandMessage);
        if (LOG.isDebugEnabled())
            LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));

        final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
        if (LOG.isDebugEnabled())
            LOG.debug("RESPONSE AFTER PROCESSING ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(responseData));

        if (responseData != null) {
            final Context updatedContext = addDataToContext(context, responseData, processId);
            if (LOG.isDebugEnabled())
                LOG.debug("COMMAND ({}) UPDATED CONTEXT (added ocid and token to outcome): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(updatedContext));

            operationService.saveOperationStep(execution, entity, updatedContext, commandMessage, responseData);
        }
    }

    private Context addDataToContext(final Context context, final JsonNode responseData, final String processId) {
        context.setCpid(processService.getText("ocid", responseData, processId));
        context.setToken(processService.getText("token", responseData, processId));
        return context;
    }
}
