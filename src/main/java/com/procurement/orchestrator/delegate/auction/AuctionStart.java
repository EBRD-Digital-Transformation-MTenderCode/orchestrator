package com.procurement.orchestrator.delegate.auction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.domain.AuctionLinks;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.AuctionRestClient;
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
import java.util.Objects;
import java.util.Set;

import static com.procurement.orchestrator.domain.commands.AuctionCommandType.START;

@Component
public class AuctionStart implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(AuctionSchedule.class);

    private final AuctionRestClient auctionRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public AuctionStart(final AuctionRestClient auctionRestClient,
                        final OperationService operationService,
                        final ProcessService processService,
                        final JsonUtil jsonUtil) {
        this.auctionRestClient = auctionRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityId());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();

        final JsonNode requestData = getAuctionStartData(jsonData, processId);;
        final JsonNode commandMessage = processService.getCommandMessage(START, context, requestData);
        LOG.debug("COMMAND ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(commandMessage));


        final ResponseEntity<ResponseDto> response = auctionRestClient.execute(commandMessage);
        LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));

        execution.setVariable("isAuctionStarted", false);
        if (requestData != null) {
            JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
            if (Objects.nonNull(responseData)) {
                final Boolean isAuctionStarted = processService.getBoolean("isAuctionStarted", responseData, processId);
                execution.setVariable("isAuctionStarted", isAuctionStarted);
                if (isAuctionStarted) {
                    context.setOperationType("tenderPeriodEndAuction");
                    final ArrayNode auctionLinksNode = (ArrayNode) responseData.get("auctionsLinks");
                    if (auctionLinksNode.size() > 0) {
                        Set<AuctionLinks> auctionLinks = new HashSet<>();
                        for (final JsonNode auctionLinkNode : auctionLinksNode) {
                            auctionLinks.add(jsonUtil.toObject(AuctionLinks.class, auctionLinkNode));
                        }
                        context.setAuctionLinks(auctionLinks);
                    }
                } else {
                    context.setOperationType("tenderPeriodEndEv");
                    context.setPhase("awarding");
                }

                final JsonNode step = processService.setAuctionStartData(jsonData, responseData, processId);
                LOG.debug("STEP FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(step));

                operationService.saveOperationStep(execution, entity, context, commandMessage, step);
            }
        }
    }

    private JsonNode getAuctionStartData(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            final JsonNode tenderNode = jsonData.get("tender");
            final ArrayNode lotsNode = (ArrayNode) jsonData.get("lots");
            final JsonNode bidsDataNode = jsonData.get("bidsData");
            if (tenderNode != null && bidsDataNode != null && lotsNode != null) {
                ((ObjectNode) tenderNode).putArray("lots").addAll(lotsNode);
                mainNode.set("tender", tenderNode);
                mainNode.set("bidsData", bidsDataNode);
            } else {
                return null;
            }
            return mainNode;
        } catch (Exception e) {
            processService.terminateProcess(processId, e.getMessage());
            return null;
        }
    }

}
