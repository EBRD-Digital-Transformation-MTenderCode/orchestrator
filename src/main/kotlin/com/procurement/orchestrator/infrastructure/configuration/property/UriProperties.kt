package com.procurement.orchestrator.infrastructure.configuration.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "uri")
class UriProperties {
    var budget: String = ""
    var tender: String = ""
}
