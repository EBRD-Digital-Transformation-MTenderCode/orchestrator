package com.procurement.orchestrator.infrastructure.client.kafka

import com.procurement.orchestrator.application.model.context.members.Incident
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.infrastructure.client.retry.Attempts
import com.procurement.orchestrator.infrastructure.client.retry.RetryInfo
import com.procurement.orchestrator.infrastructure.configuration.property.NotificationProperties
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.StringSerializer

class KafkaIncidentNotificatorClient(
    logger: Logger,
    notificationProperties: NotificationProperties,
    transform: Transform
) : AbstractKafkaNotificatorClient<Incident>(logger, transform) {

    override val topic: String = notificationProperties.incident.topic!!

    override val retryInfo = RetryInfo(
        attempts = notificationProperties.incident.retries
            ?.let { Attempts.Limited(residue = it) }
            ?: Attempts.Unlimited
    )

    override val producer: KafkaProducer<String, String> = KafkaProducer(
        notificationProperties.incident.buildProperties(),
        StringSerializer(),
        StringSerializer()
    )
}
