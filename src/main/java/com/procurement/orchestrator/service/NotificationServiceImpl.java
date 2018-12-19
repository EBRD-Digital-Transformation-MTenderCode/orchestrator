package com.procurement.orchestrator.service;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.domain.*;
import com.procurement.orchestrator.domain.dto.command.CommandMessage;
import com.procurement.orchestrator.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Value("${uri.budget}")
    private String budgetUri;
    @Value("${uri.tender}")
    private String tenderUri;

    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    private static final String PLATFORM = "platform";
    private static final String BPE = "bpe";
    private static final String URI_SEPARATOR = "/";

    public NotificationServiceImpl(final ProcessService processService,
                                   final JsonUtil jsonUtil) {
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    private String getBudgetUri(final String cpId, final String ocId) {
        String uri = budgetUri + cpId;
        if (ocId != null) {
            uri = uri + URI_SEPARATOR + ocId;
        }
        return uri;
    }

    public String getTenderUri(final String cpId, final String ocId) {
        String uri = tenderUri + cpId;
        if (ocId != null) {
            uri = uri + URI_SEPARATOR + ocId;
        }
        return uri;
    }

    private JsonNode buildOutcomes(final String type, final String id, final String token) {
        final ObjectNode outcomes = jsonUtil.createObjectNode();
        final ArrayNode outcomeArray = jsonUtil.createArrayNode();
        final ObjectNode outcomeItem = jsonUtil.createObjectNode();
        outcomeItem.put("id", id);
        if (token != null) {
            outcomeItem.put("X-TOKEN", token);
        }
        outcomeArray.add(outcomeItem);
        outcomes.replace(type.toLowerCase(), outcomeArray);
        return outcomes;
    }

    private JsonNode buildOutcomesFromContext(final List<String> outcomeTypes, final Context context) {
        final Set<Outcome> contextOutcomes = context.getOutcomes();
        if (contextOutcomes == null && contextOutcomes.isEmpty()) return null;
        final ObjectNode outcomes = jsonUtil.createObjectNode();
        for (final String outcomeType : outcomeTypes) {
            final ArrayNode outcomeArray = jsonUtil.createArrayNode();
            for (final Outcome outcome : contextOutcomes) {
                if (outcome.getType().equals(outcomeType)) {
                    final ObjectNode outcomeItem = jsonUtil.createObjectNode();
                    outcomeItem.put("id", outcome.getId());
                    if (outcome.getToken() != null) {
                        outcomeItem.put("X-TOKEN", outcome.getToken());
                    }
                    outcomeArray.add(outcomeItem);
                }
            }
            if (outcomeArray.size() > 0) outcomes.replace(outcomeType.toLowerCase(), outcomeArray);
        }
        if (outcomes.size() > 0) return outcomes;
        else return null;
    }

    public Context addEnquiryOutcomeToContext(final Context context,
                                              final JsonNode responseData,
                                              final String processId) {
        final String id = processService.getEnquiryId(responseData, processId);
        final String token = processService.getText("token", responseData, processId);
        final String ownerCA = processService.getText("ownerCA", responseData, processId);
        context.setId(id);
        context.setOwnerCA(ownerCA);
        context.setToken(token);
        return context;
    }

    public Context addBidOutcomeToContext(final Context context, final JsonNode responseData, final String processId) {
        final String id = processService.getText("bidId", responseData, processId);
        final String token = processService.getText("token", responseData, processId);
        context.setId(id);
        context.setToken(token);
        return context;
    }

    public Context addAwardOutcomeToContext(final Context context, final JsonNode responseData, final String processId) {
        Set<Outcome> outcomes;
        if (context.getOutcomes() != null) {
            outcomes = context.getOutcomes();
        } else {
            outcomes = new HashSet<>();
        }
        final ArrayNode awardsNode = (ArrayNode) responseData.get("awards");
        for (final JsonNode awardNode : awardsNode) {
            if (awardNode.get("token") != null) {
                if (!awardNode.get("status").asText().equals("unsuccessful")) {
                    outcomes.add(new Outcome(awardNode.get("id").asText(), awardNode.get("token").asText(), "awards"));
                } else {
                    outcomes.add(new Outcome(awardNode.get("id").asText(), null, "awards"));
                }
            }
        }
        context.setOutcomes(outcomes);
        return context;
    }

    public Context addCanOutcomeToContext(final Context context, final JsonNode responseData, final String processId) {
        Set<Outcome> outcomes;
        outcomes = new HashSet<>();
        final ArrayNode cansNode = (ArrayNode) responseData.get("cans");
        for (final JsonNode canNode : cansNode) {
            final Outcome outcome = new Outcome(canNode.get("contract").get("id").asText(), null, "contracts");
            outcomes.add(outcome);
        }
        context.setOutcomes(outcomes);
        return context;
    }

    public Context addContractOutcomeToContext(final Context context, final JsonNode responseData, final String processId) {
        Set<Outcome> outcomes;
        outcomes = new HashSet<>();
        final ArrayNode contractsNode = (ArrayNode) responseData.get("contracts");
        for (final JsonNode contractNode : contractsNode) {
            outcomes.add(new Outcome(contractNode.get("id").asText(), contractNode.get("token").asText(), "ac"));
        }
        context.setOutcomes(outcomes);
        return context;
    }

    public Context addNoticeOutcomeToContext(final Context context, final JsonNode responseData, final String processId) {
        context.setOcid(processService.getText("ocid", responseData, processId));
        final ArrayNode amendmentsArray = (ArrayNode) responseData.get("amendmentsIds");
        if (amendmentsArray != null) {
            Set<Outcome> outcomes;
            if (context.getOutcomes() != null) {
                outcomes = context.getOutcomes();
            } else {
                outcomes = new HashSet<>();
            }
            for (final JsonNode amendmentNode : amendmentsArray) {
                outcomes.add(new Outcome(amendmentNode.asText(), null, "amendments"));
            }
            context.setOutcomes(outcomes);
        }
        return context;
    }

    public Notification getNotificationForPlatform(final Context context) {

        final String operationType = context.getOperationType();

        final PlatformMessage message = new PlatformMessage();
        final PlatformMessageData data = new PlatformMessageData();
        message.setInitiator(PLATFORM);

        switch (OperationType.fromValue(operationType)) {
            case SUSPEND_TENDER: {
                return null;
            }
            case UNSUSPEND_TENDER: {
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                break;
            }
            case UNSUCCESSFUL_TENDER: {
                message.setInitiator(BPE);
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                data.setOutcomes(buildOutcomesFromContext(Collections.singletonList("awards"), context));
                break;
            }
            case CREATE_EI: {
                data.setOcid(context.getCpid());
                data.setUrl(getBudgetUri(context.getCpid(), null));
                data.setOutcomes(buildOutcomes("ei", context.getCpid(), context.getToken()));
                break;
            }
            case UPDATE_EI: {
                data.setOcid(context.getCpid());
                data.setUrl(getBudgetUri(context.getCpid(), context.getCpid()));
                break;
            }
            case CREATE_FS: {
                data.setOcid(context.getCpid());
                data.setUrl(getBudgetUri(context.getCpid(), null));
                data.setOutcomes(buildOutcomes("fs", context.getOcid(), context.getToken()));
                break;
            }
            case UPDATE_FS: {
                data.setOcid(context.getOcid());
                data.setUrl(getBudgetUri(context.getCpid(), context.getOcid()));
                break;
            }
            case CREATE_CN: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), null));
                data.setOutcomes(buildOutcomes(context.getStage(), context.getOcid(), context.getToken()));
                break;
            }
            case CREATE_PN: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), null));
                data.setOutcomes(buildOutcomes(context.getStage(), context.getOcid(), context.getToken()));
                break;
            }
            case CREATE_PIN: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), null));
                data.setOutcomes(buildOutcomes(context.getStage(), context.getOcid(), context.getToken()));
                break;
            }
            case UPDATE_CN: {
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                data.setOutcomes(buildOutcomesFromContext(Collections.singletonList("amendments"), context));
                break;
            }
            case CREATE_CN_ON_PN: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), null));
                data.setOutcomes(buildOutcomes(context.getStage(), context.getOcid(), null));
                break;
            }
            case CREATE_CN_ON_PIN: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), null));
                data.setOutcomes(buildOutcomes(context.getStage(), context.getOcid(), null));
                break;
            }
            case UPDATE_PN: {
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                break;
            }
            case CREATE_PIN_ON_PN: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), null));
                data.setOutcomes(buildOutcomes(context.getStage(), context.getOcid(), null));
                break;
            }
            case UPDATE_TENDER_PERIOD: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), null));
                data.setOutcomes(buildOutcomes("amendments", context.getOcid(), null));
                break;
            }
            case CREATE_ENQUIRY: {
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                data.setOutcomes(buildOutcomes("enquiries", context.getId(), null));
                break;
            }
            case ADD_ANSWER: {
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                break;
            }
            case CREATE_BID: {
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                data.setOutcomes(buildOutcomes("bids", context.getId(), context.getToken()));
                break;
            }
            case UPDATE_BID: {
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                break;
            }
            case BID_WITHDRAWN: {
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                break;
            }
            case TENDER_PERIOD_END: {
                message.setInitiator(BPE);
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                data.setOutcomes(buildOutcomesFromContext(Collections.singletonList("awards"), context));
                break;
            }
            case TENDER_PERIOD_END_EV: {
                message.setInitiator(BPE);
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                data.setOutcomes(buildOutcomesFromContext(Collections.singletonList("awards"), context));
                break;
            }
            case AUCTION_PERIOD_END: {
                message.setInitiator(BPE);
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                data.setOutcomes(buildOutcomesFromContext(Collections.singletonList("awards"), context));
                break;
            }
            case AWARD_BY_BID: {
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                break;
            }
            case AWARD_BY_BID_EV: {
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                break;
            }
            case STANDSTILL_PERIOD: {
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                break;
            }
            case STANDSTILL_PERIOD_EV: {
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                data.setOutcomes(buildOutcomesFromContext(Collections.singletonList("contracts"), context));
                break;
            }
            case AWARD_PERIOD_END: {
                message.setInitiator(BPE);
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                data.setOutcomes(buildOutcomesFromContext(Collections.singletonList("contracts"), context));
                break;
            }
            case AWARD_PERIOD_END_EV: {
                message.setInitiator(BPE);
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                data.setOutcomes(buildOutcomesFromContext(Collections.singletonList("ac"), context));
                break;
            }
            case CANCEL_STANDSTILL: {
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                data.setOutcomes(buildOutcomesFromContext(Collections.singletonList("amendments"), context));
                break;
            }
            case CANCEL_TENDER: {
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                data.setOutcomes(buildOutcomesFromContext(Arrays.asList("amendments", "awards"), context));
                break;
            }
            case CANCEL_TENDER_EV: {
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                data.setOutcomes(buildOutcomesFromContext(Arrays.asList("amendments", "awards"), context));
                break;
            }
            case CANCEL_PLAN: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), null));
                data.setOutcomes(buildOutcomes(context.getStage(), context.getOcid(), null));
                break;
            }
            case UPDATE_BID_DOCS: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                break;
            }
            case UPDATE_AC: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                break;
            }
            case ISSUING_AC: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                break;
            }
            case FINAL_UPDATE_AC: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                break;
            }
            case BUYER_SIGNING_AC: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                break;
            }
            case SUPPLIER_SIGNING_AC: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                break;
            }
            case VERIFICATION_AC: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                break;
            }
            case TREASURY_APPROVING_AC: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                break;
            }
            case ACTIVATION_AC: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                break;
            }
            case END_AWARD_PERIOD: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                break;
            }
            case UPDATE_CAN_DOCS: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                break;
            }
            case CANCEL_CAN: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                break;
            }
            case CANCEL_CAN_CONTRACT: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                break;
            }

            default:
                return null;
        }

        message.setOperationId(context.getOperationId());
        message.setResponseId(UUIDs.timeBased().toString());
        data.setOperationDate(context.getStartDate());
        message.setData(data);

        final Notification notification = new Notification(
                UUID.fromString(context.getOwner()),
                UUID.fromString(context.getOperationId()),
                jsonUtil.toJson(message));

        return notification;
    }

    @Override
    public Notification getNotificationForPlatformCA(Context context) {

        final PlatformMessage message = new PlatformMessage();
        final PlatformMessageData data = new PlatformMessageData();
        message.setInitiator(BPE);
        data.setOcid(context.getOcid());
        data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
        data.setOutcomes(buildOutcomes("enquiries", context.getId(), context.getToken()));
        message.setOperationId(context.getOperationId());
        message.setResponseId(UUIDs.timeBased().toString());
        data.setOperationDate(context.getStartDate());
        message.setData(data);

        final Notification notification = new Notification(
                UUID.fromString(context.getOwnerCA()),
                UUID.fromString(context.getOperationId()),
                jsonUtil.toJson(message));

        return notification;

    }

    @Override
    public CommandMessage getCommandMessage(Enum command, Context context, JsonNode data) {
        return new CommandMessage(
                context.getOperationId(),
                command,
                null,
                data,
                CommandMessage.ApiVersion.V_0_0_1);
    }

    @Override
    public CommandMessage getAgentCommandMessage(Enum command, Context context, JsonNode data) {
        return new CommandMessage(
                context.getOperationId(),
                command,
                context,
                data,
                CommandMessage.ApiVersion.V_0_0_1);
    }
}
