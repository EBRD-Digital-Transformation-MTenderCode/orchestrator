package com.procurement.orchestrator.kafka;

import com.procurement.orchestrator.service.ProcessService;
import org.camunda.bpm.engine.RuntimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(MessageConsumer.class);

    private final ProcessService processService;
    private final RuntimeService runtimeService;

    public MessageConsumer(final ProcessService processService,
                           final RuntimeService runtimeService) {
        this.processService = processService;
        this.runtimeService = runtimeService;
    }

    @KafkaListener(topics = "orchestrator")
    public void onReceiving(Message message,
                            @Header(KafkaHeaders.OFFSET) Integer offset,
                            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                            @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment acknowledgment) {

        log.info("Processing topic = {}, partition = {}, offset = {}, message = {}", topic, partition, offset, message);
        acknowledgment.acknowledge();
        try {
//            ProcessInstance pi = runtimeService.createProcessInstanceQuery()
//                    .processInstanceBusinessKey(message.getId())
//                    .singleResult();
//            if (Objects.isNull(pi)) {
            String processType = "testChronographConsumer";
            String transactionId = message.getId();
            processService.startProcess(processType, transactionId);
//            }
        } catch (Exception e) {
        }
    }
}
