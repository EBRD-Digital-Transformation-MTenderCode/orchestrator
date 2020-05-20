package com.procurement.orchestrator.infrastructure.configuration.property

import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.*

@ConfigurationProperties(prefix = "components", ignoreUnknownFields = false, ignoreInvalidFields = false)
class ComponentProperties(private val container: HashMap<String, Component> = HashMap()) :
    MutableMap<String, ComponentProperties.Component> by container {

    override fun get(key: String): Component = container[key]
        ?: throw IllegalStateException("Properties for component '$key' are not found.")

    class Component(
        var host: String? = null,
        var port: Int = 8080
    ) {
        val url: String
            get() = "http://${host}:${port}"
    }
}
