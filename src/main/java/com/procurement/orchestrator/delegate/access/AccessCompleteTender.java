package com.procurement.orchestrator.delegate.access;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.AccessRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.procurement.orchestrator.domain.commands.AccessCommandType.COMPLETE_TENDER;

@Component
public class AccessCompleteTender implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(AccessCompleteTender.class);

    private final AccessRestClient accessRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public AccessCompleteTender(final AccessRestClient accessRestClient,
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
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();
        if (jsonData != null) {
            final JsonNode commandMessage = processService.getCommandMessage(COMPLETE_TENDER, context, jsonData);
            JsonNode responseData = processService.processResponse(
                    accessRestClient.execute(commandMessage),
                    context,
                    processId,
                    taskId,
                    commandMessage);

            if (Objects.nonNull(responseData)) {
                operationService.saveOperationStep(
                        execution,
                        entity,
                        commandMessage,
                        processService.setTenderAndLot(jsonData, responseData, processId));

            }
        }
    }
}

