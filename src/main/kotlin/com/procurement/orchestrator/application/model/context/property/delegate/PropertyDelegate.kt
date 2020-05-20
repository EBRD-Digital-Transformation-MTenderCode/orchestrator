package com.procurement.orchestrator.application.model.context.property.delegate

import kotlin.reflect.KProperty

interface PropertyDelegate<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
}
