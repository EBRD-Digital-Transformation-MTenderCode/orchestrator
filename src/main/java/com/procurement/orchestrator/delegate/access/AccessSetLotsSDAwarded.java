package com.procurement.orchestrator.delegate.access;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.AccessRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import lombok.Getter;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.value.BooleanValue;
import org.camunda.bpm.engine.variable.value.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.procurement.orchestrator.domain.commands.AccessCommandType.SET_LOTS_SD_AWARDED;

@Component
public class AccessSetLotsSDAwarded implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(AccessSetLotsSDAwarded.class);

    private final AccessRestClient accessRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public AccessSetLotsSDAwarded(final AccessRestClient accessRestClient,
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
        final String taskId = execution.getCurrentActivityName();

        final String lotId = execution.<StringValue>getVariableTyped("lotId").getValue();
        final Boolean lotAwarded = execution.<BooleanValue>getVariableTyped("lotAwarded").getValue();
        final SetLotStatusRequest request = new SetLotStatusRequest(lotId, lotAwarded);
        final JsonNode requestData = jsonUtil.toJsonNode(request);

        final String operationId = context.getOperationId();
        final JsonNode commandMessage = processService.getCommandMessage(SET_LOTS_SD_AWARDED, context, requestData);
        LOG.debug("COMMAND TO SERVICE ({}): '{}'.", operationId, jsonUtil.toJsonOrEmpty(commandMessage));

        final ResponseEntity<ResponseDto> response = accessRestClient.execute(commandMessage);
        LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", operationId, jsonUtil.toJson(response.getBody()));

        final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
        LOG.debug("RESPONSE AFTER PROCESSING ({}): '{}'.", operationId, jsonUtil.toJsonOrEmpty(responseData));

        if (Objects.nonNull(responseData)) {
            final JsonNode step = processService.addUpdatedLot(jsonData, responseData, processId);
            LOG.debug("STEP FOR SAVE ({}): '{}'.", operationId, jsonUtil.toJsonOrEmpty(step));

            operationService.saveOperationStep(execution, entity, commandMessage, step);
        }
    }

    @Getter
    private static class SetLotStatusRequest {
        private final String lotId;
        private final boolean lotAwarded;

        SetLotStatusRequest(String lotId, boolean lotAwarded) {
            this.lotId = lotId;
            this.lotAwarded = lotAwarded;
        }
    }
}
