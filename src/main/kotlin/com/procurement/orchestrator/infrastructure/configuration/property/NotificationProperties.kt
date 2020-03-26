package com.procurement.orchestrator.infrastructure.configuration.property

import com.procurement.orchestrator.infrastructure.configuration.property.kafka.Producer
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "notification")
class NotificationProperties {
    var incident = Producer()
    var platform = Producer()
}
