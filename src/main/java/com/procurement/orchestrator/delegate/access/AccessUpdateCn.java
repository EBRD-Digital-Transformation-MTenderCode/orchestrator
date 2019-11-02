package com.procurement.orchestrator.delegate.access;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.AccessRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static com.procurement.orchestrator.domain.commands.AccessCommandType.UPDATE_CN;

@Component
public class AccessUpdateCn implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(AccessUpdateCn.class);
    private static final String VARIABLE_NAME = "isLotsChanged";

    private final AccessRestClient accessRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public AccessUpdateCn(
        final AccessRestClient accessRestClient,
        final OperationService operationService,
        final ProcessService processService,
        final JsonUtil jsonUtil
    ) {
        this.accessRestClient = accessRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityId());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();
        final JsonNode requestData = processService.getAccessData(jsonData, processId);

        final JsonNode commandMessage = processService.getCommandMessage(UPDATE_CN, context, requestData);
        if (LOG.isDebugEnabled())
            LOG.debug("COMMAND ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(commandMessage));

        final ResponseEntity<ResponseDto> response = accessRestClient.execute(commandMessage);
        if (LOG.isDebugEnabled())
            LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));

        execution.setVariable(VARIABLE_NAME, false);
        LOG.debug("COMMAND ({}) IN CONTEXT PUT THE VARIABLE '{}' WITH THE VALUE '{}'.", context.getOperationId(), VARIABLE_NAME, false);

        final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
        if (LOG.isDebugEnabled())
            LOG.debug("RESPONSE AFTER PROCESSING ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(responseData));

        if (responseData != null) {
            final Boolean isLotsChanged = processService.getBoolean(VARIABLE_NAME, responseData, processId);
            execution.setVariable(VARIABLE_NAME, isLotsChanged);
            LOG.debug("COMMAND ({}) IN CONTEXT PUT THE VARIABLE '{}' WITH THE VALUE '{}'.", context.getOperationId(), VARIABLE_NAME, isLotsChanged);

            operationService.saveOperationStep(execution, entity, context, commandMessage, responseData);
        }
    }
}
