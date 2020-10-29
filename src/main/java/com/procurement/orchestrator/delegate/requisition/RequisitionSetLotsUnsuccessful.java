package com.procurement.orchestrator.delegate.requisition;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.RequisitionRestClient;
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

import static com.procurement.orchestrator.domain.commands.RequisitionCommandType.SET_LOTS_UNSUCCESSFUL;

@Component
public class RequisitionSetLotsUnsuccessful implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(RequisitionSetLotsUnsuccessful.class);

    private final RequisitionRestClient requisitionRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public RequisitionSetLotsUnsuccessful(final RequisitionRestClient requisitionRestClient,
                                          final OperationService operationService,
                                          final ProcessService processService,
                                          final JsonUtil jsonUtil) {
        this.requisitionRestClient = requisitionRestClient;
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

        final ResponseEntity<ResponseDto> response = requisitionRestClient.execute(commandMessage);
        if (LOG.isDebugEnabled())
            LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));

        JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
        if (LOG.isDebugEnabled())
            LOG.debug("RESPONSE AFTER PROCESSING ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(responseData));

        if (Objects.nonNull(responseData)) {
            processContext(context, responseData, execution);
            if (LOG.isDebugEnabled())
                LOG.debug("CONTEXT FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(context));

            final JsonNode step = addLotsUnsuccessful(jsonData, responseData, processId);
            if (LOG.isDebugEnabled())
                LOG.debug("STEP FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(step));

            operationService.saveOperationStep(execution, entity, context, commandMessage, step);
        }
    }

    private JsonNode addLotsUnsuccessful(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.set("unsuccessfulLots", responseData.get("unsuccessfulLots"));

            final ObjectNode tenderNode = (ObjectNode) responseData.get("tender");
            mainNode.set("tenderStatus", tenderNode.get("status"));
            mainNode.set("tenderStatusDetails", tenderNode.get("statusDetails"));

            if (mainNode.has("tender")) {
                final ObjectNode mainTenderNode = (ObjectNode) mainNode.get("tender");
                mainTenderNode.set("status", tenderNode.get("status"));
                mainTenderNode.set("statusDetails", tenderNode.get("statusDetails"));
            } else {
                mainNode.set("tender", tenderNode);
            }

            return jsonData;
        } catch (Exception e) {
            processService.terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    private void processContext(final Context context, final JsonNode responseData, final DelegateExecution execution) {
        if (responseData.has("tender")) {
            final JsonNode tenderNode = responseData.get("tender");
            if (tenderNode.has("status")) {
                final JsonNode tenderStatus = tenderNode.get("status");
                if ("unsuccessful".equals(tenderStatus.asText())) {
                    context.setOperationType("tenderUnsuccessful");
                    context.setPhase("empty");

                    execution.setVariable("isTenderUnsuccessful", true);
                }
            }
        }

    }
}

