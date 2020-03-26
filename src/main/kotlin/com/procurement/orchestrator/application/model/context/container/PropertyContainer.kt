package com.procurement.orchestrator.application.model.context.container

interface PropertyContainer {
    operator fun get(name: String): Any?
    operator fun set(name: String, value: Any)
}
