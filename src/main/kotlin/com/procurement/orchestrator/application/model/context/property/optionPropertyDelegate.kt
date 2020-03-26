package com.procurement.orchestrator.application.model.context.property

import com.procurement.orchestrator.application.model.context.container.PropertyContainer
import com.procurement.orchestrator.application.model.context.property.delegate.OptionPropertyDelegate
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.asOption
import kotlin.reflect.KProperty

inline fun <reified T : Any> optionPropertyDelegate(
    propertyContainer: PropertyContainer
): OptionPropertyDelegate<T> = object : OptionPropertyDelegate<T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): Option<T> = propertyContainer[property.name]
        ?.let { it as T }
        .asOption()

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Option<T>) {
        if (value.isDefined)
            propertyContainer[property.name] = value.get
    }
}

inline fun <reified T> optionPropertyDelegate(
    transform: Transform,
    propertyContainer: PropertyContainer
): OptionPropertyDelegate<T> = object : OptionPropertyDelegate<T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): Option<T> = propertyContainer[property.name]
        ?.let { value ->
            transform.tryDeserialization(value = value.toString(), target = T::class.java)
                .doOnError { fail -> throw IllegalStateException(fail.description, fail.exception) }
                .get
        }
        .asOption()

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Option<T>) {
        if (value.isDefined)
            propertyContainer[property.name] = transform.trySerialization(value.get)
                .doOnError { fail -> throw IllegalStateException(fail.description, fail.exception) }
                .get
    }
}
