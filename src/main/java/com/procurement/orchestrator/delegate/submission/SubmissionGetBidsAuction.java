package com.procurement.orchestrator.delegate.submission;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.SubmissionRestClient;
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

import static com.procurement.orchestrator.domain.commands.SubmissionCommandType.GET_BIDS_AUCTION;

@Component
public class SubmissionGetBidsAuction implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SubmissionGetBidsAuction.class);

    private final SubmissionRestClient submissionRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public SubmissionGetBidsAuction(final SubmissionRestClient submissionRestClient,
                                    final OperationService operationService,
                                    final ProcessService processService,
                                    final JsonUtil jsonUtil) {
        this.submissionRestClient = submissionRestClient;
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

        final JsonNode lotsData = getLots(jsonData, jsonUtil.createObjectNode(), processId);
        final JsonNode commandMessage = processService.getCommandMessage(GET_BIDS_AUCTION, context, lotsData);
        LOG.debug("COMMAND ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(commandMessage));

        final ResponseEntity<ResponseDto> response = submissionRestClient.execute(commandMessage);
        LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));

        JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
        LOG.debug("RESPONSE AFTER PROCESSING ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(responseData));

        if (Objects.nonNull(responseData)) {
            final boolean isBidsDataEmpty = isBidsDataEmpty(responseData);
            execution.setVariable("availabilityOfBidsForOpening", !isBidsDataEmpty);

            if (isBidsDataEmpty) {
                context.setOperationType("tenderUnsuccessful");
                context.setPhase("empty");
                execution.setVariable("operationType", "tenderUnsuccessful");
                execution.setVariable("isAuctionStarted", false);
            }

            final JsonNode payloadWithBidsData = addBidsData(jsonData, responseData, processId);
            final JsonNode step = addBidsFromBidsData(payloadWithBidsData, responseData, processId);
            LOG.debug("STEP FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(step));

            operationService.saveOperationStep(execution, entity, context, commandMessage, step);
        }
    }

    private boolean isBidsDataEmpty(final JsonNode bidsData) {
        return !bidsData.has("bidsData") || (bidsData.get("bidsData")).size() == 0;
    }

    private JsonNode addBidsData(final JsonNode jsonData, final JsonNode bidsData, final String processId) {
        try {
            if (bidsData.has("bidsData")) {
                ((ObjectNode) jsonData).replace("bidsData", bidsData.get("bidsData"));
            }
            return jsonData;
        } catch (Exception e) {
            processService.terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    private JsonNode getLots(final JsonNode jsonData, final ObjectNode targetNode, final String processId) {
        try {
            final JsonNode tenderNode = jsonData.get("tender");
            final ArrayNode lots = (ArrayNode) tenderNode.get("lots");
            targetNode.putArray("lots").addAll(lots);
            return targetNode;
        } catch (Exception e) {
            processService.terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    private JsonNode addBidsFromBidsData(final JsonNode jsonData, final JsonNode response, final String processId) {
        try {
            final ArrayNode bidsNode = jsonUtil.createArrayNode();
            if (response.has("bidsData")) {
                final ArrayNode bidsData = (ArrayNode) response.get("bidsData");
                for (JsonNode bidData : bidsData) {
                    final ArrayNode bids = (ArrayNode) bidData.get("bids");
                    bidsNode.addAll(bids);
                }
            }
            ((ObjectNode) jsonData).putArray("bids").addAll(bidsNode);
            return jsonData;
        } catch (Exception e) {
            processService.terminateProcess(processId, e.getMessage());
            return null;
        }
    }
}
