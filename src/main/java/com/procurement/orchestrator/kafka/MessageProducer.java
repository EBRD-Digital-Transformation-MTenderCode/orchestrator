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
    private KafkaTemplate<String, Task> kafkaTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProducer.class);

    public boolean send(Task task) {
        try {

            SendResult<String, Task> sendResult = kafkaTemplate.sendDefault(task.getIdentifier(), task).get();
            RecordMetadata recordMetadata = sendResult.getRecordMetadata();
            LOGGER.info("topic = {}, partition = {}, offset = {}, task = {}", recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(), task);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
