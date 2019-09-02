package com.procurement.orchestrator.delegate.evaluation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Outcome;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.EvaluationRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static com.procurement.orchestrator.domain.commands.EvaluationCommandType.AWARDS_CANCELLATION;

@Component
public class EvaluationAwardsCancellation implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(EvaluationAwardsCancellation.class);

    private final EvaluationRestClient evaluationRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public EvaluationAwardsCancellation(
        final EvaluationRestClient evaluationRestClient,
        final OperationService operationService,
        final ProcessService processService,
        final JsonUtil jsonUtil
    ) {
        this.evaluationRestClient = evaluationRestClient;
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
        final JsonNode lots = processService.getLots(jsonData, processId);

        final JsonNode commandMessage = processService.getCommandMessage(AWARDS_CANCELLATION, context, lots);
        LOG.debug("COMMAND ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(commandMessage));

        final ResponseEntity<ResponseDto> response = evaluationRestClient.execute(commandMessage);
        LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));

        final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
        LOG.debug("RESPONSE AFTER PROCESSING ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(responseData));

        if (responseData != null) {
            context.setPhase("empty");

            final Context modifiedContext = addAwardOutcomeToContext(context, responseData);
            if (LOG.isDebugEnabled())
                LOG.debug("CONTEXT FOR SAVE (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(modifiedContext) + "'.");


            final JsonNode step = processService.addAwards(jsonData, responseData, processId);
            LOG.debug("STEP FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(step));

            operationService.saveOperationStep(execution, entity, modifiedContext, commandMessage, step);
        }
    }

    private Context addAwardOutcomeToContext(final Context context, final JsonNode responseData) {
        final Set<Outcome> outcomes;
        if (context.getOutcomes() != null) {
            outcomes = context.getOutcomes();
        } else {
            outcomes = new HashSet<>();
        }

        final ArrayNode awardsNode = (ArrayNode) responseData.get("awards");
        for (final JsonNode awardNode : awardsNode) {
            if (awardNode.has("token")) {
                final String token = awardNode.get("token").asText();
                final String status = awardNode.get("status").asText();
                final String id = awardNode.get("id").asText();

                if ("unsuccessful".equals(status)) {
                    outcomes.add(new Outcome(id, null, "awards"));
                } else {
                    outcomes.add(new Outcome(id, token, "awards"));
                }
            }

        }

        context.setOutcomes(outcomes);
        return context;
    }
}
