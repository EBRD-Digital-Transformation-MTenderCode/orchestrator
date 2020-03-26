package com.procurement.orchestrator.application.model.context.container

class DefaultPropertyContainer : PropertyContainer {
    private val container = mutableMapOf<String, Any>()

    override fun get(name: String): Any? = container[name]

    override fun set(name: String, value: Any) {
        container[name] = value
    }

    fun toMap(): Map<String, Any> = container.toMap()
}
