package com.procurement.orchestrator.infrastructure.configuration

import com.procurement.orchestrator.application.client.NotificatorClient
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.infrastructure.bpms.delegate.BpeIncident
import com.procurement.orchestrator.infrastructure.bpms.delegate.notifier.PlatformNotification
import com.procurement.orchestrator.infrastructure.client.kafka.KafkaIncidentNotificatorClient
import com.procurement.orchestrator.infrastructure.client.kafka.KafkaPlatformNotificatorClient
import com.procurement.orchestrator.infrastructure.configuration.property.NotificationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(
    NotificationProperties::class
)
class NotificatorConfiguration(
    private val notificationProperties: NotificationProperties,
    private val transform: Transform
) {

    @Bean
    fun incidentNotificatorClient(): NotificatorClient<BpeIncident> =
        KafkaIncidentNotificatorClient(notificationProperties, transform)

    @Bean
    fun platformNotificatorClient(): NotificatorClient<PlatformNotification.MessageEnvelop> =
        KafkaPlatformNotificatorClient(notificationProperties, transform)
}
