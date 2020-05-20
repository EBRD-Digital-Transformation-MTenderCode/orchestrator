package com.procurement.orchestrator.application.model.context.property

import com.procurement.orchestrator.application.model.context.container.PropertyContainer
import com.procurement.orchestrator.application.model.context.property.delegate.PropertyDelegate
import com.procurement.orchestrator.application.service.Transform
import kotlin.reflect.KProperty

inline fun <reified T : Any> propertyDelegate(
    propertyContainer: PropertyContainer,
    noinline initializer: DefaultValue<T> = NoneDefaultValue
): PropertyDelegate<T> = object : PropertyDelegate<T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = propertyContainer[property.name]
        ?.let { it as T }
        ?: initializer(property.name)

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        propertyContainer[property.name] = value
    }
}

inline fun <reified T> propertyDelegate(
    transform: Transform,
    propertyContainer: PropertyContainer,
    noinline initializer: DefaultValue<T> = NoneDefaultValue
): PropertyDelegate<T> = object : PropertyDelegate<T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = propertyContainer[property.name]
        ?.let { value ->
            transform.tryDeserialization(value = value.toString(), target = T::class.java)
                .orReturnFail { fail -> throw IllegalStateException(fail.description, fail.exception) }
        }
        ?: initializer(property.name)

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        propertyContainer[property.name] = transform.trySerialization(value)
            .orReturnFail { fail -> throw IllegalStateException(fail.description, fail.exception) }
    }
}
