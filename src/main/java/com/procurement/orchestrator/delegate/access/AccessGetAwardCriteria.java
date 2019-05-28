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

import static com.procurement.orchestrator.domain.commands.AccessCommandType.GET_AWARD_CRITERIA;

@Component
public class AccessGetAwardCriteria implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(AccessGetAwardCriteria.class);

    private final AccessRestClient accessRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public AccessGetAwardCriteria(final AccessRestClient accessRestClient,
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
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();

        final JsonNode commandMessage = processService.getCommandMessage(GET_AWARD_CRITERIA, context, jsonUtil.empty());
        LOG.debug("COMMAND (" + context.getOperationId() + "): " + jsonUtil.toJson(commandMessage));

        final ResponseEntity<ResponseDto> response = accessRestClient.execute(commandMessage);
        LOG.debug("RESPONSE FROM SERVICE (" + context.getOperationId() + "): " + response.getBody());

        final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
        LOG.debug("RESPONSE AFTER PROCESSING (" + context.getOperationId() + "): " + jsonUtil.toJson(responseData));

        if (Objects.nonNull(responseData)) {
            context.setAwardCriteria(processService.getText("awardCriteria", responseData, processId));
            LOG.debug("CONTEXT FOR SAVE (" + context.getOperationId() + "): " + jsonUtil.toJson(context));

            operationService.saveOperationStep(execution, entity, context, commandMessage);
        }
    }
}

