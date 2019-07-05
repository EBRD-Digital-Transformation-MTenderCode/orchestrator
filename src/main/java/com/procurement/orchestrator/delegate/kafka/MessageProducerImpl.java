package com.procurement.orchestrator.delegate.kafka;

import com.procurement.orchestrator.domain.Notification;
import com.procurement.orchestrator.domain.chronograph.ScheduleTask;
import com.procurement.orchestrator.domain.dto.command.CommandMessage;
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
    private static final String AUCTION_TOPIC = "auction-front-in";
    private static final String DOC_GENERATOR_TOPIC = "document-generator-in";
    private static final String MCONNECT_BUS_IN = "mconnect-bus-in";
    private final KafkaTemplate<String, String> internalKafkaTemplate;
    private final JsonUtil jsonUtil;

    public MessageProducerImpl(
            final KafkaTemplate<String, String> internalKafkaTemplate,
            final JsonUtil jsonUtil) {
        this.internalKafkaTemplate = internalKafkaTemplate;
        this.jsonUtil = jsonUtil;
    }

    public boolean sendToChronograph(final ScheduleTask task) {
        try {
            LOG.info("Attempt to send a message to the Chronograph.");
            final SendResult<String, String> sendResult = internalKafkaTemplate.send(
                    CHRONOGRAPH_TOPIC,
                    jsonUtil.toJson(task)).get();
            final RecordMetadata recordMetadata = sendResult.getRecordMetadata();
            LOG.info(
                    "Sent to the Chronograph (topic: '{}', partition: '{}', offset: '{}', task: '{}').",
                    recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(), task.toString()
            );
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean sendToPlatform(final Notification notification) {
        try {
            LOG.info("Attempt to send a message to the Platform.");
            final SendResult<String, String> sendResult = internalKafkaTemplate.send(
                    PLATFORM_TOPIC,
                    jsonUtil.toJson(notification)).get();
            final RecordMetadata recordMetadata = sendResult.getRecordMetadata();
            LOG.info(
                    "Sent to the Platform (topic: '{}', partition: '{}', offset: '{}', notification: '{}').",
                    recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(), notification.toString()
            );
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean sendToAuction(final CommandMessage commandMessage) {
        try {
            LOG.info("Attempt to send a message to the Auction.");
            final SendResult<String, String> sendResult = internalKafkaTemplate.send(
                    AUCTION_TOPIC,
                    jsonUtil.toJson(commandMessage)).get();
            final RecordMetadata recordMetadata = sendResult.getRecordMetadata();
            LOG.info(
                    "Sent to the Auction (topic: '{}', partition: '{}', offset: '{}', command: '{}').",
                    recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(), commandMessage.toString()
            );
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean sendToDocGenerator(final CommandMessage commandMessage) {
        try {
            LOG.info("Attempt to send a message to the Document-Generator.");
            final SendResult<String, String> sendResult = internalKafkaTemplate.send(
                    DOC_GENERATOR_TOPIC,
                    jsonUtil.toJson(commandMessage)).get();
            final RecordMetadata recordMetadata = sendResult.getRecordMetadata();
            LOG.info(
                    "Sent to the Document-Generator (topic: '{}', partition: '{}', offset: '{}', command: '{}').",
                    recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(), commandMessage.toString()
            );
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean sendToAgent(final CommandMessage commandMessage) {
        try {
            LOG.info("Attempt to send a message to the mConnect-Bus.");
            final SendResult<String, String> sendResult =
                internalKafkaTemplate.send(MCONNECT_BUS_IN, jsonUtil.toJson(commandMessage)).get();
            final RecordMetadata recordMetadata = sendResult.getRecordMetadata();
            LOG.info(
                "Sent to the mConnect-Bus (topic: '{}', partition: '{}', offset: '{}', command: '{}').",
                recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(), commandMessage.toString()
            );
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
