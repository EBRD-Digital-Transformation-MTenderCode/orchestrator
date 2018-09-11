package com.procurement.orchestrator.delegate.access;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.commnds.AccessCommandType;
import com.procurement.orchestrator.domain.dto.commnds.MdmCommandType;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.AccessRestClient;
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
public class AccessCheckItems implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(AccessCheckItems.class);

    private final AccessRestClient accessRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public AccessCheckItems(final AccessRestClient accessRestClient,
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
        final JsonNode prevData = jsonUtil.toJsonNode(entity.getResponseData());
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();
        execution.setVariable("mdmValidation", true);
        execution.setVariable("itemsAdd", false);
        final JsonNode rqData = processService.getCheckItems(prevData, processId);
        final JsonNode commandMessage = processService.getCommandMessage(AccessCommandType.CHECK_ITEMS.value(), context, rqData);
        JsonNode responseData = null;
        if (Objects.nonNull(rqData)) {
            responseData = processService.processResponse(
                    accessRestClient.execute(commandMessage),
                    context,
                    processId,
                    taskId,
                    commandMessage);
        }
        if (Objects.nonNull(responseData)) {

            processResponse(execution, responseData, processId);

            operationService.saveOperationStep(
                    execution,
                    entity,
                    commandMessage,
                    processService.setCheckItems(prevData, responseData, processId));
        }
    }

    private void processResponse(final DelegateExecution execution,
                                 final JsonNode responseData,
                                 final String processId) {
        final Boolean mdmValidation = processService.getBoolean("mdmValidation", responseData, processId);
        final Boolean itemsAdd = processService.getBoolean("itemsAdd", responseData, processId);
        execution.setVariable("mdmValidation", mdmValidation);
        execution.setVariable("itemsAdd", itemsAdd);
    }
}

