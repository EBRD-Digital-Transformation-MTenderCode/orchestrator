package com.procurement.orchestrator.delegate.kafka;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Rule;
import com.procurement.orchestrator.domain.chronograph.ChronographResponse;
import com.procurement.orchestrator.domain.dto.auction.AuctionData;
import com.procurement.orchestrator.service.OperationService;
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
import java.util.Objects;

public class MessageConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(MessageConsumer.class);

    private final ProcessService processService;
    private final RequestService requestService;
    private final OperationService operationService;
    private final JsonUtil jsonUtil;
    private final DateUtil dateUtil;

    public MessageConsumer(final ProcessService processService,
                           final RequestService requestService,
                           final OperationService operationService,
                           final JsonUtil jsonUtil,
                           final DateUtil dateUtil) {
        this.processService = processService;
        this.requestService = requestService;
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
    }

    @KafkaListener(topics = "chronograph-out")
    public void onChronograph(final String message, @Header(KafkaHeaders.ACKNOWLEDGMENT) final Acknowledgment ac) {
        ac.acknowledge();
        try {
            LOG.info("Get task: " + message);
            final ChronographResponse response = jsonUtil.toObject(ChronographResponse.class, message);
            final ChronographResponse.ChronographResponseData data = response.getData();
            final Context contextChronograph = jsonUtil.toObject(Context.class, data.getMetaData());
            final Context prevContext = requestService.getContext(contextChronograph.getCpid());
            final Context context = new Context();
            final Rule rules = requestService.checkAndGetRule(prevContext, contextChronograph.getProcessType());
            context.setRequestId(contextChronograph.getRequestId());
            context.setOperationId(contextChronograph.getOperationId());
            context.setCountry(rules.getCountry());
            context.setPmd(rules.getPmd());
            context.setProcessType(rules.getProcessType());
            context.setStage(rules.getNewStage());
            context.setPhase(rules.getNewPhase());
            context.setOperationType(rules.getOperationType());
            context.setOwner(prevContext.getOwner());
            context.setCpid(prevContext.getCpid());
            context.setOcid(prevContext.getOcid());
            context.setToken(prevContext.getToken());
            context.setLanguage(prevContext.getLanguage());
            context.setIsAuction(prevContext.getIsAuction());
            context.setStartDate(dateUtil.nowFormatted());
            saveRequestAndCheckOperation(context, jsonUtil.empty());
            final Map<String, Object> variables = new HashMap<>();
            variables.put("operationType", context.getOperationType());
            processService.startProcess(context, variables);
        } catch (Exception e) {
        }
    }

    @KafkaListener(topics = "auction-front-out")
    public void onAuction(final String message, @Header(KafkaHeaders.ACKNOWLEDGMENT) final Acknowledgment ac) {
        ac.acknowledge();
        try {
            LOG.info("Get task: " + message);
            final JsonNode response = jsonUtil.toJsonNode(message);
            if (response.get("errors") != null) {
                //TODO error processing
            } else {
                final JsonNode dataNode = response.get("data");
                if (dataNode != null) {
                    final String cpid = dataNode.get("tender").get("id").asText();
                    final Context prevContext = requestService.getContext(cpid);
                    final Context context = new Context();
                    final Rule rules = requestService.checkAndGetRule(prevContext, "auctionPeriodEnd");
                    final String uuid = UUIDs.timeBased().toString();
                    context.setRequestId(uuid);
                    context.setOperationId(uuid);
                    context.setCountry(rules.getCountry());
                    context.setPmd(rules.getPmd());
                    context.setProcessType(rules.getProcessType());
                    context.setStage(rules.getNewStage());
                    context.setPhase(rules.getNewPhase());
                    context.setOperationType(rules.getOperationType());
                    context.setOwner(prevContext.getOwner());
                    context.setCpid(prevContext.getCpid());
                    context.setOcid(prevContext.getOcid());
                    context.setToken(prevContext.getToken());
                    context.setLanguage(prevContext.getLanguage());
                    context.setIsAuction(prevContext.getIsAuction());
                    context.setStartDate(dateUtil.nowFormatted());
                    saveRequestAndCheckOperation(context, dataNode);
                    final Map<String, Object> variables = new HashMap<>();
                    variables.put("operationType", context.getOperationType());
                    processService.startProcess(context, variables);
                }
            }
        } catch (Exception e) {
        }
    }

    void saveRequestAndCheckOperation(final Context context, final JsonNode jsonData) {
        final JsonNode data;
        if (Objects.isNull(jsonData)) {
            data = jsonUtil.createObjectNode();
        } else {
            data = jsonData;
        }
        requestService.saveRequest(context.getRequestId(), context.getOperationId(), context, data);
        operationService.checkOperationById(context.getOperationId());
    }
}
