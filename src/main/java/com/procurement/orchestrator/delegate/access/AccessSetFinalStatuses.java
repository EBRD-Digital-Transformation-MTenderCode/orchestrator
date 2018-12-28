package com.procurement.orchestrator.delegate.access;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.OperationType;
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

import static com.procurement.orchestrator.domain.commands.AccessCommandType.SET_FINAL_STATUSES;

@Component
public class AccessSetFinalStatuses implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(AccessSetFinalStatuses.class);

    private final AccessRestClient accessRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public AccessSetFinalStatuses(final AccessRestClient accessRestClient,
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
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();
        final JsonNode lotId = processService.getLotId(jsonData, processId);
        final JsonNode commandMessage = processService.getCommandMessage(SET_FINAL_STATUSES, context, lotId);
        JsonNode responseData = processService.processResponse(
                accessRestClient.execute(commandMessage),
                context,
                processId,
                taskId,
                commandMessage);
        if (Objects.nonNull(responseData)) {
            processResponseData(execution, context, responseData, processId);
            operationService.saveOperationStep(
                    execution,
                    entity,
                    context,
                    commandMessage,
                    processService.addAccessFinalStatusesData(jsonData, responseData, processId));
        }
    }

    private void processResponseData(final DelegateExecution execution,
                                     final Context context,
                                     final JsonNode responseData,
                                     final String processId) {
        final Boolean stageEnd = processService.getBoolean("stageEnd", responseData, processId);
        final Boolean cpSuccess = processService.getBoolean("cpSuccess", responseData, processId);
        if (!stageEnd && cpSuccess) {
            context.setOperationType(OperationType.CONFIRM_CAN.value());
        } else if (stageEnd && cpSuccess) {
            context.setOperationType(OperationType.END_AWARD_PERIOD.value());
            context.setPhase("empty");
        } else if (stageEnd && !cpSuccess) {
            context.setOperationType(OperationType.END_CONTRACT_PROCESS.value());
            context.setPhase("empty");
        }
        execution.setVariable("stageEnd", stageEnd);
    }
}

