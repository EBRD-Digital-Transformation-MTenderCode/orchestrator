package com.procurement.orchestrator.config.kafka;

import com.datastax.driver.core.utils.UUIDs;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.domain.chronograph.ChronographResponse;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.service.RequestService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

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
    public void onReceiving(final String message,
                            @Header(KafkaHeaders.OFFSET) final Integer offset,
                            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) final int partition,
                            @Header(KafkaHeaders.RECEIVED_TOPIC) final String topic,
                            @Header(KafkaHeaders.ACKNOWLEDGMENT) final Acknowledgment acknowledgment) {

        acknowledgment.acknowledge();
        try {
            LOG.info("Get task: " + message);
            final ChronographResponse response = jsonUtil.toObject(ChronographResponse.class, message);
            final ChronographResponse.ChronographResponseData data = response.getData();
            final Params paramsFromChronograph = jsonUtil.toObject(Params.class, data.getMetaData());
            paramsFromChronograph.setRequestId(UUIDs.timeBased().toString());
            paramsFromChronograph.setOperationId(paramsFromChronograph.getOperationId());
            paramsFromChronograph.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
            requestService.saveRequest(
                    paramsFromChronograph.getRequestId(),
                    paramsFromChronograph.getOperationId(),
                    paramsFromChronograph,
                    jsonUtil.toJsonNode(data));
            final Map<String, Object> variables = new HashMap<>();
            variables.put("checkEnquiries", 0);
            processService.startProcess(paramsFromChronograph, variables);
        } catch (Exception e) {
        }
    }
}
