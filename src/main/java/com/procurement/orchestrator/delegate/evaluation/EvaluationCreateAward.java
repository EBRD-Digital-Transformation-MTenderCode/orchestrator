package com.procurement.orchestrator.delegate.evaluation;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.EvaluationRestClient;
import com.procurement.orchestrator.service.NotificationService;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.procurement.orchestrator.domain.commands.EvaluationCommandType.CREATE_AWARD;

@Component
public class EvaluationCreateAward implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(EvaluationCreateAward.class);

    private final EvaluationRestClient evaluationRestClient;
    private final NotificationService notificationService;
    private final OperationService operationService;
    private final ProcessService processService;
    private final RuntimeService runtimeService;
    private final JsonUtil jsonUtil;

    public EvaluationCreateAward(final EvaluationRestClient evaluationRestClient,
                                 final NotificationService notificationService,
                                 final OperationService operationService,
                                 final ProcessService processService,
                                 final RuntimeService runtimeService,
                                 final JsonUtil jsonUtil) {
        this.evaluationRestClient = evaluationRestClient;
        this.notificationService = notificationService;
        this.operationService = operationService;
        this.processService = processService;
        this.runtimeService = runtimeService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityId());
        final String processId = execution.getProcessInstanceId();
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final String taskId = execution.getCurrentActivityId();
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());

        final JsonNode commandMessage = processService.getCommandMessage(CREATE_AWARD, context, jsonData);
        LOG.debug("COMMAND ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(commandMessage));

        final ResponseEntity<ResponseDto> response = evaluationRestClient.execute(commandMessage);
        LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));

        final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
        LOG.debug("RESPONSE AFTER PROCESSING ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(responseData));

        if (Objects.nonNull(responseData)) {
            if (responseData.has("lotAwarded")) {
                final JsonNode lotAwardedNode = responseData.get("lotAwarded");
                final boolean lotAwarded = lotAwardedNode.booleanValue();
                execution.setVariable("lotAwarded", lotAwarded);
                LOG.debug("COMMAND ({}) IN CONTEXT PUT THE VARIABLE 'lotAwarded' WITH THE VALUE '{}'.", context.getOperationId(), lotAwarded);
            }

            final Context updatedContext = notificationService.addOneAwardOutcomeToContext(context, responseData, processId);
            LOG.debug("COMMAND ({}) UPDATED CONTEXT (added one award to outcome): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(updatedContext));
            if (updatedContext == null) {
                LOG.error("eEvaluation do not return attributes the 'token' and the 'award'.");
                runtimeService.deleteProcessInstance(processId, context.getOperationId());
                return;
            }

            final JsonNode step = processService.addOneAwardData(jsonData, responseData, processId);
            LOG.debug("STEP FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(step));

            operationService.saveOperationStep(execution, entity, updatedContext, commandMessage, step);
        }
    }
}


