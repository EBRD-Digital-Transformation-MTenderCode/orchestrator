package com.procurement.orchestrator.application.model.context.property.delegate

import com.procurement.orchestrator.domain.functional.Option
import kotlin.reflect.KProperty

interface OptionPropertyDelegate<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Option<T>
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Option<T>)
}
