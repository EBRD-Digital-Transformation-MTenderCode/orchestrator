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
import org.camunda.bpm.engine.variable.value.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static com.procurement.orchestrator.domain.commands.AccessCommandType.CHECK_LOT_AWARDED;

@Component
public class AccessCheckLotAwarded implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(AccessCheckLotAwarded.class);

    private final AccessRestClient accessRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public AccessCheckLotAwarded(final AccessRestClient accessRestClient,
                                 final OperationService operationService,
                                 final ProcessService processService,
                                 final JsonUtil jsonUtil) {
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
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();


        final Context contextForCommand = new Context();
        final String lotId = execution.<StringValue>getVariableTyped("lotId").getValue();
        contextForCommand.setId(lotId);
        final JsonNode commandMessage = processService.getCommandMessage(CHECK_LOT_AWARDED, contextForCommand, jsonUtil.empty());
        if (LOG.isDebugEnabled())
            LOG.debug("COMMAND (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(commandMessage) + "'.");

        final ResponseEntity<ResponseDto> response = accessRestClient.execute(commandMessage);
        if (LOG.isDebugEnabled()) {
            final ResponseDto body = response.getBody();
            final String result = jsonUtil.toJson(body);
            LOG.debug("RESPONSE FROM SERVICE (" + context.getOperationId() + "): '" + result + "'.");
        }

        final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
        if (LOG.isDebugEnabled())
            LOG.debug("RESPONSE AFTER PROCESSING (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(responseData) + "'.");

        if (responseData != null) {
            operationService.saveOperationStep(execution, entity, commandMessage);
        }
    }
}

