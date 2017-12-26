package com.procurement.orchestrator.kafka;


import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;


@Service
public class MessageProducer {

    @Autowired
    private KafkaTemplate<String, Message> kafkaTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProducer.class);

    public boolean send(Message message) {
        try {

            SendResult<String, Message> sendResult = kafkaTemplate.sendDefault(message.getId().toString(), message).get();
            RecordMetadata recordMetadata = sendResult.getRecordMetadata();
            LOGGER.info("topic = {}, partition = {}, offset = {}, message = {}", recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(), message);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
