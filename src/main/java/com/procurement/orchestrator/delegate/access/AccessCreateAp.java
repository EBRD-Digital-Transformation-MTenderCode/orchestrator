package com.procurement.orchestrator.delegate.access;

import com.fasterxml.jackson.databind.JsonNode;
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

import static com.procurement.orchestrator.domain.commands.AccessCommandType.CREATE_AP;

@Component
public class AccessCreateAp implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(AccessCreateAp.class);

    private final AccessRestClient accessRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public AccessCreateAp(final AccessRestClient accessRestClient,
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
        final JsonNode requestData = getAccessData(jsonData, processId);

        if (requestData != null) {
            final JsonNode commandMessage = processService.getCommandMessage(CREATE_AP, context, requestData);
            LOG.debug("COMMAND ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(commandMessage));

            final ResponseEntity<ResponseDto> response = accessRestClient.execute(commandMessage);
            LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));

            JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
            LOG.debug("RESPONSE AFTER PROCESSING ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(responseData));

            if (responseData != null) {

                final Context updatedContext = addDataToContext(context, responseData, processId);
                LOG.debug("COMMAND ({}) CHANGED CONTEXT: '{}'.", context.getOperationId(), updatedContext);

                final JsonNode step = setAccessData(jsonData, responseData, processId);
                LOG.debug("STEP FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(step));

                operationService.saveOperationStep(execution, entity, updatedContext, commandMessage, step);

            }
        }
    }

    private Context addDataToContext(final Context context, final JsonNode responseData, final String processId) {
        context.setOcid(processService.getText("ocid", responseData, processId));
        context.setCpid(processService.getText("cpid", responseData, processId));
        context.setToken(processService.getText("token", responseData, processId));
        return context;
    }

    private JsonNode getAccessData(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.set("tender", jsonData.get("tender"));
            return mainNode;
        } catch (Exception e) {
            processService.terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    private JsonNode setAccessData(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("tender", responseData.get("tender"));
            mainNode.replace("parties", responseData.get("parties"));
            return mainNode;
        } catch (Exception e) {
            processService.terminateProcess(processId, e.getMessage());
            return null;
        }
    }
}
