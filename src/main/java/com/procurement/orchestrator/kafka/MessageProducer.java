package com.procurement.orchestrator.kafka;

import com.procurement.orchestrator.domain.chronograph.ChronographTask;
import com.procurement.orchestrator.domain.PlatformMessage;
import com.procurement.orchestrator.utils.JsonUtil;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class MessageProducer {

//    private static final Logger LOG = LoggerFactory.getLogger(MessageProducer.class);
//    private static final String CHRONOGRAPH_TOPIC = "chronograph-in";
//    private static final String PLATFORM_TOPIC = "2c974565-e527-4e7d-b369-bf5db76c4f5c";
//    private final KafkaTemplate<String, String> internalKafkaTemplate;
//    private final KafkaTemplate<String, String> externalKafkaTemplate;
//    private final JsonUtil jsonUtil;
//
//    public MessageProducer(
//            final KafkaTemplate<String, String> internalKafkaTemplate,
//            final KafkaTemplate<String, String> externalKafkaTemplate,
//            final JsonUtil jsonUtil) {
//        this.internalKafkaTemplate = internalKafkaTemplate;
//        this.externalKafkaTemplate = externalKafkaTemplate;
//        this.jsonUtil = jsonUtil;
//    }

    public boolean sendToChronograph(ChronographTask task) {
//        try {
//            SendResult<String, String> sendResult = internalKafkaTemplate.send(
//                    CHRONOGRAPH_TOPIC,
//                    jsonUtil.toJson(task)).get();
//            RecordMetadata recordMetadata = sendResult.getRecordMetadata();
//            LOG.info("Send to chronograph: ", recordMetadata.topic(),
//                    recordMetadata.partition(), recordMetadata.offset(), task.toString());
//            return true;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        return true;
    }

    public boolean sendToPlatform(PlatformMessage message) {
//        try {
//            SendResult<String, String> sendResult = externalKafkaTemplate.send(
//                    PLATFORM_TOPIC,
//                    jsonUtil.toJson(message)).get();
//            RecordMetadata recordMetadata = sendResult.getRecordMetadata();
//            LOG.info("Send to platform: ", recordMetadata.topic(),
//                    recordMetadata.partition(), recordMetadata.offset(), message.toString());
//            return true;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        return true;
    }
}
