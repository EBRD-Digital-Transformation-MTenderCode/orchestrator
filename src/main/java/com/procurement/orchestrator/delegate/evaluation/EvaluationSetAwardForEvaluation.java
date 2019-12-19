package com.procurement.orchestrator.delegate.evaluation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.domain.Context;
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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.procurement.orchestrator.domain.commands.EvaluationCommandType.SET_AWARD_FOR_EVALUATION;

@Component
public class EvaluationSetAwardForEvaluation implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(EvaluationSetAwardForEvaluation.class);

    private final EvaluationRestClient evaluationRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public EvaluationSetAwardForEvaluation(
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
        final String taskId = execution.getCurrentActivityId();
        final String processId = execution.getProcessInstanceId();

        final JsonNode commandMessage = processService.getCommandMessage(SET_AWARD_FOR_EVALUATION, context, jsonData);
        if (LOG.isDebugEnabled())
            LOG.debug("COMMAND ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(commandMessage));

        final ResponseEntity<ResponseDto> response = evaluationRestClient.execute(commandMessage);
        if (LOG.isDebugEnabled())
            LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));

        final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
        if (LOG.isDebugEnabled())
            LOG.debug("RESPONSE AFTER PROCESSING ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(responseData));

        if (responseData != null) {
            final JsonNode step = updateAwards(context.getOperationId(), jsonData, responseData, processId);
            if (LOG.isDebugEnabled())
                LOG.debug("STEP FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(step));

            operationService.saveOperationStep(execution, entity, context, commandMessage, step);
        }
    }

    private JsonNode updateAwards(
        final String operationId,
        final JsonNode jsonData,
        final JsonNode responseData,
        final String processId
    ) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            if (mainNode.has("awards")) {
                final ArrayNode awardsNode = (ArrayNode) mainNode.get("awards");
                final Map<String, JsonNode> awardsByIds = groupingAwardsByIds(awardsNode);

                final ArrayNode createdAwards = (ArrayNode) responseData.get("awards");
                final Collection<JsonNode> updatedAwards = replaceAwards(awardsByIds, createdAwards);

                final ArrayNode updatedAwardsNode = jsonUtil.createArrayNode();
                updatedAwardsNode.addAll(updatedAwards);

                mainNode.set("awards", updatedAwardsNode);
            } else {
                mainNode.set("awards", responseData.get("awards"));
            }
            return jsonData;
        } catch (Exception e) {
            LOG.error("COMMAND (" + operationId + "): Could not add awards.", e);
            processService.terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    private Map<String, JsonNode> groupingAwardsByIds(final ArrayNode awardsNode) {
        final LinkedHashMap<String, JsonNode> awardsByIds = new LinkedHashMap<>(awardsNode.size());
        for (JsonNode awardNode : awardsNode) {
            final String awardId = awardNode.get("id").asText();
            awardsByIds.put(awardId, awardNode);
        }
        return awardsByIds;
    }

    private Collection<JsonNode> replaceAwards(final Map<String, JsonNode> awardsByIds, final ArrayNode createdAwards) {
        final LinkedHashMap<String, JsonNode> updatedAwardsByIds = new LinkedHashMap<>(awardsByIds);
        for (JsonNode createdAward : createdAwards) {
            final String idCreatedAward = createdAward.get("id").asText();
            updatedAwardsByIds.put(idCreatedAward, createdAward);
        }
        return updatedAwardsByIds.values();
    }
}
