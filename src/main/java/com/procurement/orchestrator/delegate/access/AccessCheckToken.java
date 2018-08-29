package com.procurement.orchestrator.delegate.access;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.CommandMessage;
import com.procurement.orchestrator.domain.dto.CommandType;
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
public class AccessCheckToken implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(AccessCheckToken.class);

    private final AccessRestClient accessRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public AccessCheckToken(final AccessRestClient accessRestClient,
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
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();
        final JsonNode commandMessage = processService.getCommandMessage(CommandType.CHECK_TOKEN, context, jsonUtil.empty());
        final JsonNode responseData = processService.processResponse(
                accessRestClient.execute(commandMessage),
                context,
                processId,
                taskId,
                jsonUtil.toJsonNode(commandMessage));
        if (Objects.nonNull(responseData)) {
            operationService.saveOperationStep(execution, entity);
        }
    }
}

