package com.procurement.orchestrator.delegate.kafka;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.chronograph.ChronographResponse;
import com.procurement.orchestrator.domain.commands.AgentCommandType;
import com.procurement.orchestrator.domain.commands.AuctionCommandType;
import com.procurement.orchestrator.domain.commands.DocGeneratorCommandType;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.service.RequestService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

import java.util.HashMap;
import java.util.Map;

public class MessageConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(MessageConsumer.class);

    private final ProcessService processService;
    private final RequestService requestService;
    private final JsonUtil jsonUtil;
    private final DateUtil dateUtil;

    public MessageConsumer(final ProcessService processService,
                           final RequestService requestService,
                           final JsonUtil jsonUtil,
                           final DateUtil dateUtil) {
        this.processService = processService;
        this.requestService = requestService;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
    }

    @KafkaListener(topics = "chronograph-out")
    public void onChronograph(final String message, @Header(KafkaHeaders.ACKNOWLEDGMENT) final Acknowledgment ac) {
        try {
            LOG.info("Received a message from the Chronograph (" + message + ").");
            ac.acknowledge();
            final ChronographResponse response = jsonUtil.toObject(ChronographResponse.class, message);
            if (response.getStatus().equals("NOTIFICATION")) {
                final ChronographResponse.ChronographResponseData data = response.getData();
                final Context contextChronograph = jsonUtil.toObject(Context.class, data.getMetaData());
                final Context prevContext = requestService.getContext(contextChronograph.getCpid());
                final Context context = requestService.checkRulesAndProcessContext(
                        prevContext,
                        contextChronograph.getProcessType(),
                        contextChronograph.getRequestId());
                requestService.saveRequestAndCheckOperation(context, jsonUtil.empty());
                final Map<String, Object> variables = new HashMap<>();
                variables.put("operationType", context.getOperationType());
                processService.startProcess(context, variables);
            }
        } catch (Exception e) {
            //TODO error processing
            LOG.error("Error while processing the message from the Chronograph (" + message + ").", e);
        }
    }

    @KafkaListener(topics = "auction-front-out")
    public void onAuction(final String message, @Header(KafkaHeaders.ACKNOWLEDGMENT) final Acknowledgment ac) {
        try {
            LOG.info("Received a message from the Auction (" + message + ").");
            ac.acknowledge();
            final JsonNode response = jsonUtil.toJsonNode(message);
            if (response.get("errors") == null) {
                final String command = response.get("command").asText();
                switch (AuctionCommandType.fromValue(command)) {
                    case END: {
                        final JsonNode data = response.get("data");
                        if (data != null) {
                            final String cpid = data.get("tender").get("id").asText();
                            final Context prevContext = requestService.getContext(cpid);
                            final String uuid = UUIDs.timeBased().toString();
                            final Context context = requestService.checkRulesAndProcessContext(
                                    prevContext,
                                    "auctionPeriodEnd",
                                    uuid);
                            requestService.saveRequestAndCheckOperation(context, data);
                            final Map<String, Object> variables = new HashMap<>();
                            variables.put("operationType", context.getOperationType());
                            processService.startProcess(context, variables);
                        }
                        break;
                    }
                    default: {
                        final String uuid = UUIDs.timeBased().toString();
                        requestService.saveRequest(uuid, uuid, null, response.get("data"));
                        break;
                    }
                }
            } else {
                final String uuid = UUIDs.timeBased().toString();
                requestService.saveRequest(uuid, uuid, null, response.get("errors"));
            }
        } catch (Exception e) {
            //TODO error processing
            LOG.error("Error while processing the message from the Auction (" + message + ").", e);
        }
    }

    @KafkaListener(topics = "document-generator-out")
    public void onDocGenerator(final String message, @Header(KafkaHeaders.ACKNOWLEDGMENT) final Acknowledgment ac) {
        try {
            LOG.info("Received a message from the Document-Generator (" + message + ").");
            ac.acknowledge();
            final JsonNode response = jsonUtil.toJsonNode(message);
            if (response.get("errors") == null) {
                final String command = response.get("command").asText();
                switch (DocGeneratorCommandType.fromValue(command)) {
                    case CONTRACT_FINALIZATION: {
                        final JsonNode data = response.get("data");
                        if (data != null) {
                            final String ocid = data.get("ocid").asText();
                            final Context prevContext = requestService.getContext(ocid);
                            final String uuid = UUIDs.timeBased().toString();
                            final Context context = requestService.checkRulesAndProcessContext(
                                    prevContext,
                                    "finalUpdateAC",
                                    uuid);
                            requestService.saveRequestAndCheckOperation(context, data);
                            final Map<String, Object> variables = new HashMap<>();
                            variables.put("operationType", context.getOperationType());
                            processService.startProcess(context, variables);
                        }
                        break;
                    }
                    default: {
                        final String uuid = UUIDs.timeBased().toString();
                        requestService.saveRequest(uuid, uuid, null, response.get("data"));
                        break;
                    }
                }
            } else {
                final String uuid = UUIDs.timeBased().toString();
                requestService.saveRequest(uuid, uuid, null, response.get("errors"));
            }
        } catch (Exception e) {
            //TODO error processing
            LOG.error("Error while processing the message from the Document-Generator (" + message + ").", e);
        }
    }

    @KafkaListener(topics = "transport-agent-out")
    public void onAgent(final String message, @Header(KafkaHeaders.ACKNOWLEDGMENT) final Acknowledgment ac) {
        try {
            LOG.info("Received a message from the mConnect-Bus (" + message + ").");
            ac.acknowledge();
            final JsonNode response = jsonUtil.toJsonNode(message);
            if (response.get("errors") == null) {
                final String command = response.get("command").asText();
                switch (AgentCommandType.fromValue(command)) {
                    case VERIFICATION: {
                        final JsonNode dataNode = response.get("data");
                        if (dataNode != null) {
                            final String ocid = dataNode.get("ocid").asText();
                            final Context prevContext = requestService.getContext(ocid);
                            final String uuid = UUIDs.timeBased().toString();
                            final Context context = requestService.checkRulesAndProcessContext(
                                    prevContext,
                                    "verificationAC",
                                    uuid);
                            requestService.saveRequestAndCheckOperation(context, dataNode);
                            final Map<String, Object> variables = new HashMap<>();
                            variables.put("operationType", context.getOperationType());
                            processService.startProcess(context, variables);
                        }
                        break;
                    }
                    case TREASURY_APPROVING: {
                        final JsonNode dataNode = response.get("data");
                        if (dataNode != null) {
                            final String ocid = dataNode.get("id_dok").asText();
                            final Context prevContext = requestService.getContext(ocid);
                            final String uuid = UUIDs.timeBased().toString();
                            final Context context = requestService.checkRulesAndProcessContext(
                                    prevContext,
                                    "treasuryApprovingAC",
                                    uuid);
                            requestService.saveRequestAndCheckOperation(context, dataNode);
                            final Map<String, Object> variables = new HashMap<>();
                            variables.put("operationType", context.getOperationType());
                            processService.startProcess(context, variables);
                        }
                        break;
                    }
                    default: {
                        final String uuid = UUIDs.timeBased().toString();
                        requestService.saveRequest(uuid, uuid, null, response.get("data"));
                        break;
                    }
                }
            } else {
                final String uuid = UUIDs.timeBased().toString();
                requestService.saveRequest(uuid, uuid, null, response.get("errors"));
            }
        } catch (Exception e) {
            //TODO error processing
            LOG.error("Error while processing the message from the mConnect-Bus (" + message + ").", e);
        }
    }

}
