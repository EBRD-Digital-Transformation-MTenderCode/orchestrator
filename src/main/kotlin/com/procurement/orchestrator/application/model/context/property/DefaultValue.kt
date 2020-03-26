package com.procurement.orchestrator.application.model.context.property

typealias DefaultValue<T> = (name: String) -> T

object NoneDefaultValue : DefaultValue<Nothing> {
    override fun invoke(name: String): Nothing {
        throw NoSuchElementException("No such element '${name}'.")
    }
}
