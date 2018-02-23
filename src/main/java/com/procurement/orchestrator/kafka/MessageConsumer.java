package com.procurement.orchestrator.kafka;

import com.procurement.orchestrator.kafka.dto.ChronographTask;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.HashMap;
import java.util.Map;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MessageConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(MessageConsumer.class);

    private final ProcessService processService;
    private final RuntimeService runtimeService;
    private final JsonUtil jsonUtil;

    public MessageConsumer(final ProcessService processService,
                           final RuntimeService runtimeService,
                           final JsonUtil jsonUtil) {
        this.processService = processService;
        this.runtimeService = runtimeService;
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
            ChronographTask task = jsonUtil.toObject(ChronographTask.class, message);
            ChronographTask.TaskMetaData metaData = jsonUtil.toObject(ChronographTask.TaskMetaData.class,
                    task.getMetaData());
            Map<String, Object> variables = new HashMap<>();
            variables.put("checkEnquiries", 0);
            processService.startProcess("tenderPeriodEnd", metaData.getOperationId(), variables);
        } catch (Exception e) {
        }
    }
}
