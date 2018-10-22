package com.procurement.orchestrator.delegate.notification;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.delegate.kafka.MessageProducer;
import com.procurement.orchestrator.domain.*;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.service.NotificationService;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
public class SendMessageAuctionToPlatform implements JavaDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(SendMessageAuctionToPlatform.class);

    private final NotificationService notificationService;
    private final OperationService operationService;
    private final MessageProducer messageProducer;
    private final JsonUtil jsonUtil;

    public SendMessageAuctionToPlatform(final NotificationService notificationService,
                                        final OperationService operationService,
                                        final MessageProducer messageProducer,
                                        final JsonUtil jsonUtil) {
        this.notificationService = notificationService;
        this.operationService = operationService;
        this.messageProducer = messageProducer;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        //auction links
        final Set<AuctionLinks> auctionLinks = context.getAuctionLinks();
        for (final AuctionLinks links : auctionLinks) {
            final String owner = links.getOwner();
            final PlatformMessage message = new PlatformMessage();
            final PlatformMessageData data = new PlatformMessageData();
            message.setInitiator("bpe");
            data.setOcid(context.getOcid());
            data.setUrl(notificationService.getTenderUri(context.getCpid(), context.getOcid()));
            final ObjectNode outcomes = jsonUtil.createObjectNode();
            //links
            final ArrayNode linksArray = jsonUtil.createArrayNode();
            for (final AuctionLinks.AuctionLink link : links.getLinks()) {
                final ObjectNode linksItem = jsonUtil.createObjectNode();
                linksItem.put("relatedBid", link.getRelatedBid());
                linksItem.put("url", link.getUrl());
                linksArray.add(linksItem);
            }
            outcomes.replace("links", linksArray);
            //unsuccessful awards
            if (owner.equals(context.getOwner())) {
                final Set<Outcome> contextOutcomes = context.getOutcomes();
                if (contextOutcomes != null) {
                    final ArrayNode outcomeArray = jsonUtil.createArrayNode();
                    for (final Outcome outcome : contextOutcomes) {
                        final ObjectNode outcomeItem = jsonUtil.createObjectNode();
                        outcomeItem.put("id", outcome.getId());
                        if (outcome.getToken() != null) {
                            outcomeItem.put("X-TOKEN", outcome.getToken());
                        }
                        outcomeArray.add(outcomeItem);
                    }
                    outcomes.replace("awards", outcomeArray);
                }
            }
            data.setOutcomes(outcomes);
            message.setOperationId(context.getOperationId());
            message.setResponseId(UUIDs.timeBased().toString());
            data.setOperationDate(context.getStartDate());
            message.setData(data);

            final Notification notification = new Notification(
                    UUID.fromString(owner),
                    UUID.fromString(context.getOperationId()),
                    jsonUtil.toJson(message));
            messageProducer.sendToPlatform(notification);

            operationService.saveOperationStep(
                    execution,
                    entity,
                    context,
                    jsonUtil.toJsonNode(notification));

        }
    }
}
