package com.procurement.orchestrator.delegate.access;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

import static com.procurement.orchestrator.domain.commands.AccessCommandType.CHECK_ITEMS;

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
        LOG.info(execution.getCurrentActivityId());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final JsonNode prevData = jsonUtil.toJsonNode(entity.getResponseData());
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();
        execution.setVariable("mdmValidation", true);
        execution.setVariable("itemsAdd", false);
        final JsonNode rqData = processService.getCheckItems(prevData, processId);
        if (rqData != null) {
            final JsonNode commandMessage = processService.getCommandMessage(CHECK_ITEMS, context, rqData);
            if (LOG.isDebugEnabled())
                LOG.debug("COMMAND (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(commandMessage) + "'.");

            final ResponseEntity<ResponseDto> response = accessRestClient.execute(commandMessage);
            if (LOG.isDebugEnabled())
                LOG.debug("RESPONSE FROM SERVICE (" + context.getOperationId() + "): '" + jsonUtil.toJson(response.getBody()) + "'.");

            final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
            if (LOG.isDebugEnabled())
                LOG.debug("RESPONSE AFTER PROCESSING (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(responseData) + "'.");

            if (responseData != null) {
                processResponse(execution, responseData, processId);
                final JsonNode step = setCheckItems(prevData, responseData, processId);
                if (LOG.isDebugEnabled())
                    LOG.debug("STEP FOR SAVE (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(step) + "'.");

                operationService.saveOperationStep(execution, entity, commandMessage, step);
            }
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

    private JsonNode setCheckItems(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode tenderNode = (ObjectNode) jsonData.get("tender");
            final JsonNode tenderResponseNode = responseData.get("tender");
            if (tenderResponseNode != null) {
                tenderNode.replace("classification", tenderResponseNode.get("classification"));
            }
            final ObjectNode data = (ObjectNode) jsonData;

            final JsonNode mainProcurementCategory = responseData.get("mainProcurementCategory");
            if (mainProcurementCategory != null) data.put("mainProcurementCategory", mainProcurementCategory.asText());

            final JsonNode items = responseData.get("items");
            if (items != null) data.putArray("items").addAll((ArrayNode) items);;

            data.put("mdmValidation", responseData.get("mdmValidation").asBoolean());
            data.put("itemsAdd", responseData.get("itemsAdd").asBoolean());

            return jsonData;
        } catch (Exception e) {
            processService.terminateProcess(processId, e.getMessage());
            return null;
        }
    }
}

