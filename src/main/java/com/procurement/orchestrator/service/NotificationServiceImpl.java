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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

    private JsonNode getOutcomes(final String outcomeName, final String id, final String token) {
        final ObjectNode outcomes = jsonUtil.createObjectNode();
        final ArrayNode outcomeArray = jsonUtil.createArrayNode();
        final ObjectNode outcomeItem = jsonUtil.createObjectNode();
        outcomeItem.put("id", id);
        if (token != null) {
            outcomeItem.put("X-TOKEN", token);
        }
        outcomeArray.add(outcomeItem);
        outcomes.replace(outcomeName.toLowerCase(), outcomeArray);
        return outcomes;
    }

    private JsonNode getOutcomes(final String outcomeName, final Set<Outcome> contextOutcomes) {
        final ObjectNode outcomes = jsonUtil.createObjectNode();
        final ArrayNode outcomeArray = jsonUtil.createArrayNode();
        if (contextOutcomes != null && !contextOutcomes.isEmpty()) {
            for (final Outcome outcome : contextOutcomes) {
                final ObjectNode outcomeItem = jsonUtil.createObjectNode();
                outcomeItem.put("id", outcome.getId());
                if (outcome.getToken() != null) {
                    outcomeItem.put("X-TOKEN", outcome.getToken());
                }
                outcomeArray.add(outcomeItem);
            }
            outcomes.replace(outcomeName.toLowerCase(), outcomeArray);
            return outcomes;
        }
        return null;
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
        final Set<Outcome> outcomes = new HashSet<>();
        final ArrayNode awardsNode = (ArrayNode) responseData.get("awards");
        for (final JsonNode awardNode : awardsNode) {
            if (awardNode.get("token") != null) {
                if (!awardNode.get("status").asText().equals("unsuccessful")) {
                    outcomes.add(new Outcome(awardNode.get("id").asText(), awardNode.get("token").asText()));
                } else {
                    outcomes.add(new Outcome(awardNode.get("id").asText(), null));
                }
            }
        }
        context.setOutcomes(outcomes);
        return context;
    }

    public Context addCanOutcomeToContext(final Context context, final JsonNode responseData, final String processId) {
        final Set<Outcome> outcomes = new HashSet<>();
        final ArrayNode cansNode = (ArrayNode) responseData.get("cans");
        for (final JsonNode canNode : cansNode) {
            final Outcome outcome = new Outcome(canNode.get("contract").get("id").asText(), null);
            outcomes.add(outcome);
        }
        context.setOutcomes(outcomes);
        return context;
    }

    public Context addContractOutcomeToContext(final Context context, final JsonNode responseData, final String processId) {
        final Set<Outcome> outcomes = new HashSet<>();
        final ArrayNode contractsNode = (ArrayNode) responseData.get("contracts");
        for (final JsonNode contractNode : contractsNode) {
            outcomes.add(new Outcome(contractNode.get("id").asText(), contractNode.get("token").asText()));
        }
        context.setOutcomes(outcomes);
        return context;
    }

    public Context addNoticeOutcomeToContext(final Context context, final JsonNode responseData, final String processId) {
        context.setOcid(processService.getText("ocid", responseData, processId));
        final ArrayNode amendmentsArray = (ArrayNode) responseData.get("amendmentsIds");
        if (amendmentsArray != null) {
            final Set<Outcome> outcomes = new HashSet<>();
            for (final JsonNode amendmentNode : amendmentsArray) {
                outcomes.add(new Outcome(amendmentNode.asText(), null));
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
                return null;
            }
            case CREATE_EI: {
                data.setOcid(context.getCpid());
                data.setUrl(getBudgetUri(context.getCpid(), null));
                data.setOutcomes(getOutcomes("ei", context.getCpid(), context.getToken()));
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
                data.setOutcomes(getOutcomes("fs", context.getOcid(), context.getToken()));
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
                data.setOutcomes(getOutcomes(context.getStage(), context.getOcid(), context.getToken()));
                break;
            }
            case CREATE_PN: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), null));
                data.setOutcomes(getOutcomes(context.getStage(), context.getOcid(), context.getToken()));
                break;
            }
            case CREATE_PIN: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), null));
                data.setOutcomes(getOutcomes(context.getStage(), context.getOcid(), context.getToken()));
                break;
            }
            case UPDATE_CN: {
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                data.setOutcomes(getOutcomes("amendments", context.getOutcomes()));
                break;
            }
            case CREATE_CN_ON_PN: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), null));
                data.setOutcomes(getOutcomes(context.getStage(), context.getOcid(), null));
                break;
            }
            case CREATE_CN_ON_PIN: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), null));
                data.setOutcomes(getOutcomes(context.getStage(), context.getOcid(), null));
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
                data.setOutcomes(getOutcomes(context.getStage(), context.getOcid(), null));
                break;
            }
            case UPDATE_TENDER_PERIOD: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), null));
                data.setOutcomes(getOutcomes("amendments", context.getOcid(), null));
                break;
            }
            case CREATE_ENQUIRY: {
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                data.setOutcomes(getOutcomes("enquiries", context.getId(), null));
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
                data.setOutcomes(getOutcomes("bids", context.getId(), context.getToken()));
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
                data.setOutcomes(getOutcomes("awards", context.getOutcomes()));
                break;
            }
            case TENDER_PERIOD_END_EV: {
                message.setInitiator(BPE);
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                data.setOutcomes(getOutcomes("awards", context.getOutcomes()));
                break;
            }
            case AUCTION_PERIOD_END: {
                message.setInitiator(BPE);
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                data.setOutcomes(getOutcomes("awards", context.getOutcomes()));
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
                break;
            }
            case AWARD_PERIOD_END: {
                message.setInitiator(BPE);
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                data.setOutcomes(getOutcomes("contracts", context.getOutcomes()));
                break;
            }
            case AWARD_PERIOD_END_EV: {
                message.setInitiator(BPE);
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                data.setOutcomes(getOutcomes("contracts", context.getOutcomes()));
                break;
            }
            case CANCEL_STANDSTILL: {
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                data.setOutcomes(getOutcomes("amendments", context.getOutcomes()));
                break;
            }
            case CANCEL_TENDER: {
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                data.setOutcomes(getOutcomes("amendments", context.getOutcomes()));
                break;
            }
            case CANCEL_TENDER_EV: {
                data.setOcid(context.getOcid());
                data.setUrl(getTenderUri(context.getCpid(), context.getOcid()));
                data.setOutcomes(getOutcomes("amendments", context.getOutcomes()));
                break;
            }
            case CANCEL_PLAN: {
                data.setOcid(context.getCpid());
                data.setUrl(getTenderUri(context.getCpid(), null));
                data.setOutcomes(getOutcomes(context.getStage(), context.getOcid(), null));
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
        data.setOutcomes(getOutcomes("enquiries", context.getId(), context.getToken()));
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
}
