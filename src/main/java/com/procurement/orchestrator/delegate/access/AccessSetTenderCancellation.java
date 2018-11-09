package com.procurement.orchestrator.delegate.access;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Stage;
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

import static com.procurement.orchestrator.domain.commands.AccessCommandType.SET_TENDER_CANCELLATION;

@Component
public class AccessSetTenderCancellation implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(AccessSetTenderCancellation.class);

    private final AccessRestClient accessRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public AccessSetTenderCancellation(final AccessRestClient accessRestClient,
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
        if (context.getStage().equals(Stage.EV.value())) {
            context.setOperationType("cancelTenderEv");
        } else {
            context.setOperationType("cancelTender");
        }
        context.setRequestId(UUIDs.timeBased().toString());
        context.setStartDate(context.getEndDate());
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();
        final JsonNode commandMessage = processService.getCommandMessage(SET_TENDER_CANCELLATION, context, jsonUtil.empty());
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
                    context,
                    commandMessage,
                    processService.addLots(jsonData, responseData, processId));
        }
    }

}

