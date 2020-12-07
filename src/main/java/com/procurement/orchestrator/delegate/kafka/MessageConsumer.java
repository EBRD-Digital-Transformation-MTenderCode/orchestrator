package com.procurement.orchestrator.delegate.kafka;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.OcidMatcher;
import com.procurement.orchestrator.domain.Stage;
import com.procurement.orchestrator.domain.commands.AgentResponseCommandType;
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
                            final String tenderId = data.get("tender").get("id").asText();
                            final boolean tenderIdIsOcid = OcidMatcher.isOcid(tenderId);

                            String id;
                            if (tenderIdIsOcid) {
                                id = getIdByStage(data, tenderId);
                            } else
                                id = tenderId;
                            Context prevContext = requestService.getContext(id);

                            final Stage stage = Stage.fromValue(prevContext.getStage());
                            final String processName = getProcessName(stage);

                            final String uuid = UUIDs.timeBased().toString();
                            final Context context = requestService.checkRulesAndProcessContext(
                                    prevContext,
                                    processName,
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

    private String getIdByStage(JsonNode data, String tenderId) {
        String id = null;
        final Stage stage = Stage.fromOcid(tenderId);
        switch (stage) {
            case PC:
                id = tenderId;
                break;

            case AC:
            case AP:
            case EI:
            case EV:
            case FE:
            case FS:
            case NP:
            case PIN:
            case PN:
            case PQ:
            case PS:
            case TP:
                id = data.get("cpid").asText();
                break;
        }
        return id;
    }

    private String getProcessName(Stage stage) {
        String processName = null;
        switch (stage) {
            case PC:
                processName = "auctionPeriodEndInPcr";
                break;

            case AC:
            case AP:
            case EI:
            case EV:
            case FE:
            case FS:
            case NP:
            case PIN:
            case PN:
            case PQ:
            case PS:
            case TP:
                processName = "auctionPeriodEnd";
                break;
        }
        return processName;
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

    @KafkaListener(topics = "mconnect-bus-out")
    public void onAgent(final String message, @Header(KafkaHeaders.ACKNOWLEDGMENT) final Acknowledgment ac) {
        try {
            LOG.info("Received a message from the mConnect-Bus (" + message + ").");
            ac.acknowledge();
            final JsonNode response = jsonUtil.toJsonNode(message);
            if (response.get("errors") == null) {
                final String command = response.get("command").asText();
                switch (AgentResponseCommandType.fromValue(command)) {
                    case LAUNCH_AC_VERIFICATION: {
                        launchProcess(response, "verificationAC");
                        break;
                    }
                    case TREASURY_APPROVING: {
                        launchProcess(response, "treasuryApprovingAC");
                        break;
                    }
                    case TREASURY_CLARIFICATION: {
                        launchProcess(response, "acClarification");
                        break;
                    }
                    case PROCESS_AC_REJECTION: {
                        launchProcess(response, "acRejection");
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

    private void launchProcess(final JsonNode response, final String processType) {
        final JsonNode dataNode = response.get("data");
        if (dataNode != null) {
            final String ocid = dataNode.get("ocid").asText();
            final Context prevContext = requestService.getContext(ocid);
            final String uuid = UUIDs.timeBased().toString();
            final Context context =
                    requestService.checkRulesAndProcessContext(prevContext, processType, uuid);
            requestService.saveRequestAndCheckOperation(context, dataNode);
            final Map<String, Object> variables = new HashMap<>();
            variables.put("operationType", context.getOperationType());
            processService.startProcess(context, variables);
        }
    }

}
