package com.procurement.orchestrator.config.kafka;

import com.procurement.orchestrator.application.service.Logger;
import com.procurement.orchestrator.application.service.ProcessLauncher;
import com.procurement.orchestrator.application.service.Transform;
import com.procurement.orchestrator.delegate.kafka.MessageConsumer;
import com.procurement.orchestrator.infrastructure.message.chronograph.ChronographMessageConsumer;
import com.procurement.orchestrator.infrastructure.message.docgenerator.DocGeneratorMessageConsumer;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.service.RequestService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

@Profile("default")
@Configuration
@EnableKafka
@EnableConfigurationProperties(KafkaConsumerProperties.class)
public class KafkaConsumerConfig {

    private final KafkaConsumerProperties kafkaProperties;

    public KafkaConsumerConfig(final KafkaConsumerProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, String> factory = new
                ConcurrentKafkaListenerContainerFactory<>();
        factory.setConcurrency(1);
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(AbstractMessageListenerContainer.AckMode.MANUAL);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerProps());
    }

    @Bean
    public Map<String, Object> consumerProps() {
        final Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getInternalBootstrap());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroup());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        return props;
    }

    @Bean
    public MessageConsumer messageConsumer(final ProcessService processService,
                                           final RequestService requestService,
                                           final JsonUtil jsonUtil,
                                           final DateUtil dateUtil) {
        return new MessageConsumer(processService, requestService, jsonUtil, dateUtil);
    }


    @Bean
    public ChronographMessageConsumer chronographMessageConsumer(final ProcessService processService,
                                                                 final RequestService requestService,
                                                                 final Transform transform,
                                                                 final ProcessLauncher processLauncher,
                                                                 final Logger logger) {
        return new ChronographMessageConsumer(processService, requestService, transform, processLauncher, logger);
    }

    @Bean
    public DocGeneratorMessageConsumer docGeneratorMessageConsumer(final ProcessService processService,
                                                                   final RequestService requestService,
                                                                   final Transform transform,
                                                                   final ProcessLauncher processLauncher,
                                                                   final Logger logger) {
        return new DocGeneratorMessageConsumer(processService, requestService, transform, processLauncher);
    }
}
