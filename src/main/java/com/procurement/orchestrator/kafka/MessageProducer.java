package com.procurement.orchestrator.kafka;


import com.procurement.orchestrator.utils.JsonUtil;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class MessageProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProducer.class);
    private final KafkaTemplate<String, String> taskKafkaTemplate;
    private final JsonUtil jsonUtil;

    public MessageProducer(final KafkaTemplate<String, String> taskKafkaTemplate,
                           final JsonUtil jsonUtil) {
        this.taskKafkaTemplate = taskKafkaTemplate;
        this.jsonUtil = jsonUtil;
    }

    public boolean send(Task task) {
        try {
            SendResult<String, String> sendResult = taskKafkaTemplate.sendDefault(task.getIdentifier(), jsonUtil.toJson(task)).get();
            RecordMetadata recordMetadata = sendResult.getRecordMetadata();
            LOGGER.info("topic = {}, partition = {}, offset = {}, task = {}", recordMetadata.topic(),
                    recordMetadata.partition(), recordMetadata.offset(), task.toString());
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
