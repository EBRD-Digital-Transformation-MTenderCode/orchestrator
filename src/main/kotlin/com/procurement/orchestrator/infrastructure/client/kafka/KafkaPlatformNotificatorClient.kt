package com.procurement.orchestrator.infrastructure.client.kafka

import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.infrastructure.bpms.delegate.notifier.PlatformNotification
import com.procurement.orchestrator.infrastructure.client.retry.Attempts
import com.procurement.orchestrator.infrastructure.client.retry.RetryInfo
import com.procurement.orchestrator.infrastructure.configuration.property.NotificationProperties
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.StringSerializer

class KafkaPlatformNotificatorClient(
    notificationProperties: NotificationProperties,
    transform: Transform
) : AbstractKafkaNotificatorClient<PlatformNotification.MessageEnvelop>(transform) {

    override val topic: String = notificationProperties.platform.topic!!

    override val retryInfo = RetryInfo(
        attempts = notificationProperties.platform.retries
            ?.let { Attempts.Limited(residue = it) }
            ?: Attempts.Limited()
    )

    override val producer: KafkaProducer<String, String> = KafkaProducer(
        notificationProperties.platform.buildProperties(),
        StringSerializer(),
        StringSerializer()
    )
}
