package com.procurement.orchestrator.infrastructure.client.web

sealed class Target<T>(
    val typeRef: Class<T>
) {

    companion object {
        inline fun <reified T> single(): Target<T> = Single(typeRef = T::class.java)
        inline fun <reified T> plural(): Target<T> = Plural(typeRef = T::class.java)
    }

    class Single<T>(typeRef: Class<T>) : Target<T>(typeRef)
    class Plural<T>(typeRef: Class<T>) : Target<T>(typeRef)
}
