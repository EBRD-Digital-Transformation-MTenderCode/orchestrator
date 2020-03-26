package com.procurement.orchestrator.application.model.context.property

import com.procurement.orchestrator.application.model.context.container.PropertyContainer
import com.procurement.orchestrator.application.model.context.property.delegate.NullablePropertyDelegate
import com.procurement.orchestrator.application.service.Transform
import kotlin.reflect.KProperty

inline fun <reified T> nullablePropertyDelegate(
    propertyContainer: PropertyContainer
): NullablePropertyDelegate<T> = object : NullablePropertyDelegate<T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? = propertyContainer[property.name]
        ?.let { it as T }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        if (value != null)
            propertyContainer[property.name] = value
    }
}

inline fun <reified T> nullablePropertyDelegate(
    transform: Transform,
    propertyContainer: PropertyContainer
): NullablePropertyDelegate<T> = object : NullablePropertyDelegate<T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? = propertyContainer[property.name]
        ?.let { value ->
            transform.tryDeserialization(value = value.toString(), target = T::class.java)
                .doOnError { fail -> throw IllegalStateException(fail.description, fail.exception) }
                .get
        }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        if (value != null)
            propertyContainer[property.name] = transform.trySerialization(value)
                .doOnError { fail -> throw IllegalStateException(fail.description, fail.exception) }
                .get
    }
}
