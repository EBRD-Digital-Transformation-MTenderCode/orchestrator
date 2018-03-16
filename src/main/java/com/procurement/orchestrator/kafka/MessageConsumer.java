package com.procurement.orchestrator.kafka;

import com.datastax.driver.core.utils.UUIDs;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.cassandra.service.RequestService;
import com.procurement.orchestrator.domain.chronograph.ChronographResponse;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(MessageConsumer.class);

    private final ProcessService processService;
    private final RequestService requestService;
    private final JsonUtil jsonUtil;

    public MessageConsumer(final ProcessService processService,
                           final RequestService requestService,
                           final JsonUtil jsonUtil) {
        this.processService = processService;
        this.requestService = requestService;
        this.jsonUtil = jsonUtil;
    }

    @KafkaListener(topics = "chronograph-out")
    public void onReceiving(String message,
                            @Header(KafkaHeaders.OFFSET) Integer offset,
                            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                            @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment acknowledgment) {

        acknowledgment.acknowledge();
        try {
            LOG.info("Get task: " + message);
            final ChronographResponse response = jsonUtil.toObject(ChronographResponse.class, message);
            final ChronographResponse.ChronographResponseData data = response.getData();
            final Params params = jsonUtil.toObject(Params.class, data.getMetaData());
            params.setRequestId(UUIDs.timeBased().toString());
            params.setOperationId(params.getRequestId());
            requestService.saveRequest(
                    params.getRequestId(),
                    params.getOperationId(),
                    params,
                    jsonUtil.toJsonNode(data));
            Map<String, Object> variables = new HashMap<>();
            variables.put("checkEnquiries", 0);
            variables.put("requestId", params.getRequestId());
            processService.startProcess("tenderPeriodEnd", params.getOperationId(), variables);
        } catch (Exception e) {
        }
    }
}
