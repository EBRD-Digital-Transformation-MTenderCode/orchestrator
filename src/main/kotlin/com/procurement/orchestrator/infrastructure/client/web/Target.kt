package com.procurement.orchestrator.infrastructure.client.web

sealed class Target<T>(
    val typeRef: Class<T>
) {
    class Single<T>(typeRef: Class<T>) : Target<T>(typeRef)
    class Plural<T>(typeRef: Class<T>, val defaultResult: () -> T) : Target<T>(typeRef)
}
