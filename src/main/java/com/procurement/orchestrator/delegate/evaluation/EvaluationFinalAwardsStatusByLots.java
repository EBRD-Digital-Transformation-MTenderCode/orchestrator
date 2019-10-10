package com.procurement.orchestrator.delegate.evaluation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.EvaluationRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import lombok.Getter;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static com.procurement.orchestrator.domain.commands.EvaluationCommandType.FINAL_AWARDS_STATUS_BY_LOTS;
import static com.procurement.orchestrator.domain.commands.SubmissionCommandType.FINAL_BIDS_STATUS_BY_LOTS;
import static java.util.stream.Collectors.toMap;

@Component
public class EvaluationFinalAwardsStatusByLots implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(EvaluationFinalAwardsStatusByLots.class);

    private final EvaluationRestClient evaluationRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public EvaluationFinalAwardsStatusByLots(
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

        final JsonNode commandData = generateCommandData(jsonData, processId);
        if (commandData == null) return;

        final JsonNode commandMessage = processService.getCommandMessage(FINAL_AWARDS_STATUS_BY_LOTS, context, commandData);
        if (LOG.isDebugEnabled())
            LOG.debug("COMMAND ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(commandMessage));

        final ResponseEntity<ResponseDto> response = evaluationRestClient.execute(commandMessage);
        if (LOG.isDebugEnabled())
            LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));

        final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
        if (LOG.isDebugEnabled())
            LOG.debug("RESPONSE AFTER PROCESSING ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(responseData));

        if (responseData != null) {
            final JsonNode step = updateAwardsStatuses(jsonData, responseData, processId);
            if (step == null) return;
            if (LOG.isDebugEnabled())
                LOG.debug("STEP FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(step));

            operationService.saveOperationStep(execution, entity, commandMessage, step);
        }
    }

    private JsonNode generateCommandData(final JsonNode jsonData, final String processId) {
        try {
            final List<FinalAwardsStatusByLotsRequest.Lot> lots = new ArrayList<>();
            jsonData.get("tender")
                .get("lots")
                .forEach(lot -> {
                        final String id = lot.get("id").asText();
                        lots.add(new FinalAwardsStatusByLotsRequest.Lot(id));
                    }
                );

            final FinalAwardsStatusByLotsRequest request = new FinalAwardsStatusByLotsRequest(lots);
            return jsonUtil.toJsonNode(request);
        } catch (Exception exception) {
            LOG.error("Error building data section of '" + FINAL_AWARDS_STATUS_BY_LOTS.value() + "' command.", exception);
            processService.terminateProcess(processId, exception.getMessage());
            return null;
        }
    }

    private JsonNode updateAwardsStatuses(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final FinalAwardsStatusByLotsResponse response =
                jsonUtil.toObject(FinalAwardsStatusByLotsResponse.class, responseData);
            final Map<String, FinalAwardsStatusByLotsResponse.Award> awardsByIds = response.awards
                .stream()
                .collect(toMap(FinalAwardsStatusByLotsResponse.Award::getId, Function.identity()));

            jsonData.get("awards")
                .forEach(award -> {
                        final String id = award.get("id").asText();
                        final FinalAwardsStatusByLotsResponse.Award responseAward = awardsByIds.get(id);
                        if (responseAward != null) {
                            final ObjectNode updatedAward = ((ObjectNode) award);
                            updatedAward.set("status", new TextNode(responseAward.status));
                            updatedAward.set("statusDetails", new TextNode(responseAward.statusDetails));
                        }
                    }
                );
            return jsonData;
        } catch (Exception exception) {
            LOG.error("Error processing response for '" + FINAL_BIDS_STATUS_BY_LOTS.value() + "' command.", exception);
            processService.terminateProcess(processId, exception.getMessage());
            return null;
        }
    }

    @Getter
    static class FinalAwardsStatusByLotsRequest {
        @JsonProperty("lots")
        private final List<Lot> lots;


        FinalAwardsStatusByLotsRequest(final List<Lot> lots) {
            Objects.requireNonNull(lots);
            this.lots = lots;
        }

        @Getter
        static class Lot {
            @JsonProperty("id")
            private final String id;

            Lot(final String id) {
                Objects.requireNonNull(id);
                this.id = id;
            }
        }
    }

    @Getter
    static class FinalAwardsStatusByLotsResponse {
        @JsonProperty("awards")
        private final List<Award> awards;

        @JsonCreator
        FinalAwardsStatusByLotsResponse(@JsonProperty("awards") final List<Award> awards) {
            Objects.requireNonNull(awards);
            this.awards = awards;
        }

        @Getter
        static class Award {
            @JsonProperty("id")
            private final String id;

            @JsonProperty("status")
            private final String status;

            @JsonProperty("statusDetails")
            private final String statusDetails;

            @JsonCreator
            Award(
                @JsonProperty("id") final String id,
                @JsonProperty("status") final String status,
                @JsonProperty("statusDetails") final String statusDetails
            ) {
                Objects.requireNonNull(id);
                Objects.requireNonNull(status);
                Objects.requireNonNull(statusDetails);
                this.id = id;
                this.status = status;
                this.statusDetails = statusDetails;
            }
        }
    }
}
