package com.procurement.orchestrator.delegate.access;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.AccessRestClient;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.procurement.orchestrator.domain.commands.AccessCommandType.GET_LOTS_FOR_AUCTION;

@Component
public class AccessGetLotsForAuction implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(AccessGetLotsForAuction.class);

    private final AccessRestClient accessRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public AccessGetLotsForAuction(
        final AccessRestClient accessRestClient,
        final OperationService operationService,
        final ProcessService processService,
        final JsonUtil jsonUtil
    ) {
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

        final JsonNode commandData = generateCommandData(jsonData, processId);

        final JsonNode commandMessage = processService.getCommandMessage(GET_LOTS_FOR_AUCTION, context, commandData);
        if (LOG.isDebugEnabled())
            LOG.debug("COMMAND (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(commandMessage) + "'.");

        final ResponseEntity<ResponseDto> response = accessRestClient.execute(commandMessage);
        if (LOG.isDebugEnabled())
            LOG.debug("RESPONSE FROM SERVICE (" + context.getOperationId() + "): '" + jsonUtil.toJson(response.getBody()) + "'.");

        final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
        if (LOG.isDebugEnabled())
            LOG.debug("RESPONSE AFTER PROCESSING (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(responseData) + "'.");

        if (responseData != null) {
            final JsonNode step = addLotsForAuction(jsonData, responseData, processId);
            if (LOG.isDebugEnabled())
                LOG.debug("STEP FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(step));

            operationService.saveOperationStep(execution, entity, context, commandMessage, step);
        }
    }

    private JsonNode generateCommandData(final JsonNode jsonData, final String processId) {
        try {
            final List<GetLotsForAuctionRequest.Lot> lots = new ArrayList<>();
            jsonData.get("lots")
                .forEach(lot -> {
                        final String id = lot.get("id").asText();
                        final ObjectNode valueNode = (ObjectNode) lot.get("value");
                        final BigDecimal amount = valueNode.get("amount").decimalValue();
                        final String currency = valueNode.get("currency").asText();
                        final GetLotsForAuctionRequest.Lot.Value value =
                            new GetLotsForAuctionRequest.Lot.Value(amount, currency);
                        lots.add(new GetLotsForAuctionRequest.Lot(id, value));
                    }
                );

            final GetLotsForAuctionRequest request = new GetLotsForAuctionRequest(lots);
            return jsonUtil.toJsonNode(request);
        } catch (Exception exception) {
            LOG.error("Error building data section of '" + GET_LOTS_FOR_AUCTION.value() + "' command.", exception);
            processService.terminateProcess(processId, exception.getMessage());
            return null;
        }
    }

    private JsonNode addLotsForAuction(final JsonNode jsonData, final JsonNode lotsData, final String processId) {
        try {
            final JsonNode auctionLots = lotsData.get("auctionLots");
            ((ObjectNode) jsonData).set("auctionLots", auctionLots);
            return jsonData;
        } catch (Exception e) {
            processService.terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Getter
    static class GetLotsForAuctionRequest {
        @JsonProperty("lots")
        private final List<Lot> lots;

        GetLotsForAuctionRequest(final List<Lot> lots) {
            Objects.requireNonNull(lots);
            this.lots = lots;
        }

        @Getter
        static class Lot {
            @JsonProperty("id")
            private final String id;

            @JsonProperty("value")
            private final Value value;

            Lot(final String id, final Value value) {
                Objects.requireNonNull(id);
                Objects.requireNonNull(value);
                this.id = id;
                this.value = value;
            }

            @Getter
            static class Value {
                @JsonProperty("amount")
                private final BigDecimal amount;

                @JsonProperty("currency")
                private final String currency;

                Value(final BigDecimal amount, final String currency) {
                    Objects.requireNonNull(amount);
                    Objects.requireNonNull(currency);
                    this.amount = amount;
                    this.currency = currency;
                }
            }
        }
    }
}

