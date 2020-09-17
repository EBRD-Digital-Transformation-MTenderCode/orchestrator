package com.procurement.orchestrator.delegate.access;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

import static com.procurement.orchestrator.domain.commands.AccessCommandType.CREATE_FE;

@Component
public class AccessCreateFe implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(AccessCreateFe.class);

    private final AccessRestClient accessRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public AccessCreateFe(final AccessRestClient accessRestClient,
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
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();

        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        final JsonNode tender = processService.getFullTender(jsonData, processId);

        if (tender != null) {
            final JsonNode commandMessage = processService.getCommandMessage(CREATE_FE, context, tender);
            JsonNode responseData = processService.processResponse(
                    accessRestClient.execute(commandMessage),
                    context,
                    processId,
                    taskId,
                    commandMessage
            );
            if (responseData != null) {
                final JsonNode step = replaceTender(jsonData, responseData, processId);
                final Context updatedContext = addDataToContext(context, responseData, processId);
                if (LOG.isDebugEnabled())
                    LOG.debug("STEP FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(step));

                operationService.saveOperationStep(execution, entity, updatedContext, commandMessage, step);
            }
        }
    }

    private JsonNode replaceTender(JsonNode jsonData, JsonNode responseData, String processId) {
        try {
            final JsonNode tender = responseData.get("tender");
            ((ObjectNode) jsonData).replace("tender", tender);
            return jsonData;
        } catch (Exception e) {
            processService.terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    private Context addDataToContext(final Context context, final JsonNode responseData, final String processId) {
        context.setToken(processService.getText("token", responseData, processId));
        context.setOcidCn(processService.getText("ocid", responseData, processId));
        return context;
    }
}

