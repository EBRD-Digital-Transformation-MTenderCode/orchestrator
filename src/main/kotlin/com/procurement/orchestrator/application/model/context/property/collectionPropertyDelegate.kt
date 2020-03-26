package com.procurement.orchestrator.application.model.context.property

import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.procurement.orchestrator.application.model.context.container.PropertyContainer
import com.procurement.orchestrator.application.model.context.property.delegate.CollectionPropertyDelegate
import com.procurement.orchestrator.application.service.Transform
import kotlin.reflect.KProperty

inline fun <reified V, reified T : Collection<V>> collectionPropertyDelegate(
    propertyContainer: PropertyContainer,
    noinline initializer: DefaultValue<T> = NoneDefaultValue
): CollectionPropertyDelegate<V, T> = object : CollectionPropertyDelegate<V, T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = propertyContainer[property.name]
        ?.let { it as T }
        ?: initializer(property.name)

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        propertyContainer[property.name] = value
    }
}

inline fun <reified V, reified T : Collection<V>> collectionPropertyDelegate(
    transform: Transform,
    propertyContainer: PropertyContainer,
    noinline initializer: DefaultValue<T> = NoneDefaultValue
): CollectionPropertyDelegate<V, T> = object : CollectionPropertyDelegate<V, T> {

    private val typeRef = jacksonTypeRef<T>()

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = propertyContainer[property.name]
        ?.let { value ->
            transform.tryDeserialization(value = value.toString(), typeRef = typeRef)
                .doOnError { fail -> throw IllegalStateException(fail.description, fail.exception) }
                .get
        }
        ?: initializer(property.name)

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        propertyContainer[property.name] = transform.trySerialization(value)
            .doOnError { fail -> throw IllegalStateException(fail.description, fail.exception) }
            .get
    }
}
