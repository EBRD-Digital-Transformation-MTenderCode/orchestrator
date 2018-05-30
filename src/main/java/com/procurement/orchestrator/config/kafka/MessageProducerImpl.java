package com.procurement.orchestrator.config.kafka;

import com.procurement.orchestrator.domain.Notification;
import com.procurement.orchestrator.domain.PlatformMessage;
import com.procurement.orchestrator.domain.chronograph.ScheduleTask;
import com.procurement.orchestrator.utils.JsonUtil;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

public class MessageProducerImpl implements MessageProducer {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProducerImpl.class);
    private static final String CHRONOGRAPH_TOPIC = "chronograph-in";
    private static final String PLATFORM_TOPIC = "notification-kafka-channel";
    private final KafkaTemplate<String, String> internalKafkaTemplate;
    private final KafkaTemplate<String, String> externalKafkaTemplate;
    private final JsonUtil jsonUtil;

    public MessageProducerImpl(
            final KafkaTemplate<String, String> internalKafkaTemplate,
            final KafkaTemplate<String, String> externalKafkaTemplate,
            final JsonUtil jsonUtil) {
        this.internalKafkaTemplate = internalKafkaTemplate;
        this.externalKafkaTemplate = externalKafkaTemplate;
        this.jsonUtil = jsonUtil;
    }

    public boolean sendToChronograph(final ScheduleTask task) {
        try {
            final SendResult<String, String> sendResult = internalKafkaTemplate.send(
                    CHRONOGRAPH_TOPIC,
                    jsonUtil.toJson(task)).get();
            final RecordMetadata recordMetadata = sendResult.getRecordMetadata();
            LOG.info("Send to chronograph: ", recordMetadata.topic(),
                    recordMetadata.partition(), recordMetadata.offset(), task.toString());
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean sendToPlatform(final Notification notification) {
        try {
            final SendResult<String, String> sendResult = internalKafkaTemplate.send(
                    PLATFORM_TOPIC,
                    jsonUtil.toJson(notification)).get();
            final RecordMetadata recordMetadata = sendResult.getRecordMetadata();
            LOG.info("Send to platform: ", recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(), notification.toString());
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
