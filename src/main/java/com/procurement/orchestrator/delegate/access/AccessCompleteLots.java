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

import static com.procurement.orchestrator.domain.OperationType.END_AWARD_PERIOD;
import static com.procurement.orchestrator.domain.commands.AccessCommandType.COMPLETE_LOTS;

@Component
public class AccessCompleteLots implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(AccessCompleteLots.class);

    private final AccessRestClient accessRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public AccessCompleteLots(
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

        if (jsonData != null) {
            final JsonNode commandMessage = processService.getCommandMessage(COMPLETE_LOTS, context, jsonData);
            if (LOG.isDebugEnabled())
                LOG.debug("COMMAND ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(commandMessage));

            final ResponseEntity<ResponseDto> response = accessRestClient.execute(commandMessage);
            if (LOG.isDebugEnabled())
                LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));

            final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
            if (LOG.isDebugEnabled())
                LOG.debug("RESPONSE AFTER PROCESSING ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(responseData));

            if (responseData != null) {
                final Boolean stageEnd = processService.getBoolean("stageEnd", responseData, processId);
                execution.setVariable("stageEnd", stageEnd);
                if (LOG.isDebugEnabled())
                    LOG.debug("COMMAND ({}) IN CONTEXT PUT THE VARIABLE 'stageEnd' WITH THE VALUE '{}'.", context.getOperationId(), stageEnd);

                if (stageEnd) {
                    final String prevOperationType = context.getOperationType();
                    final String newOperationType = END_AWARD_PERIOD.value();
                    context.setOperationType(newOperationType);
                    execution.setVariable("operationType", newOperationType);
                    if (LOG.isDebugEnabled())
                        LOG.debug("COMMAND ({}) IN CONTEXT CHANGED THE OPERATION TYPE FROM '{}' TO '{}'.", context.getOperationId(), prevOperationType, newOperationType);
                }

                final JsonNode step = processService.setCompleteLotsData(jsonData, responseData, processId);
                if (LOG.isDebugEnabled())
                    LOG.debug("STEP FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(step));

                operationService.saveOperationStep(execution, entity, context, commandMessage, step);
            }
        } else {
            LOG.info("Data from previous stage is empty.");
        }
    }
}
