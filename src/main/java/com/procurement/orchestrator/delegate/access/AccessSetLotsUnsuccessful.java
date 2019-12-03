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

import java.util.Objects;

import static com.procurement.orchestrator.domain.commands.AccessCommandType.SET_LOTS_UNSUCCESSFUL;

@Component
public class AccessSetLotsUnsuccessful implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(AccessSetLotsUnsuccessful.class);

    private final AccessRestClient accessRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public AccessSetLotsUnsuccessful(final AccessRestClient accessRestClient,
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
        final JsonNode unsuccessfulLots = processService.getUnsuccessfulLots(jsonData, processId);

        final JsonNode commandMessage = processService.getCommandMessage(SET_LOTS_UNSUCCESSFUL, context, unsuccessfulLots);
        if (LOG.isDebugEnabled())
            LOG.debug("COMMAND ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(commandMessage));

        final ResponseEntity<ResponseDto> response = accessRestClient.execute(commandMessage);
        if (LOG.isDebugEnabled())
            LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));

        JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
        if (LOG.isDebugEnabled())
            LOG.debug("RESPONSE AFTER PROCESSING ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(responseData));

        if (Objects.nonNull(responseData)) {
            final boolean hasTender = hasTender(responseData);
            execution.setVariable("availabilityOfTender", hasTender);

            processContext(context, responseData);
            if (LOG.isDebugEnabled())
                LOG.debug("CONTEXT FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(context));

            final JsonNode step = processService.addLotsUnsuccessful(jsonData, responseData, processId);
            if (LOG.isDebugEnabled())
                LOG.debug("STEP FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(step));

            operationService.saveOperationStep(execution, entity, context, commandMessage, step);
        }
    }

    private boolean hasTender(final JsonNode responseData) {
        return responseData.has("tender");
    }

    private void processContext(final Context context, final JsonNode responseData) {
        if (responseData.has("tender")) {
            final JsonNode tenderNode = responseData.get("tender");
            if (tenderNode.has("status")) {
                final JsonNode tenderStatus = tenderNode.get("status");
                if ("unsuccessful".equals(tenderStatus.asText())) {
                    context.setOperationType("tenderUnsuccessful");
                    context.setPhase("empty");
                }
            }
        }
    }
}

