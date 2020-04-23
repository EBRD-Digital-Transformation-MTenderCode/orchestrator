package com.procurement.orchestrator.domain.model

import com.procurement.orchestrator.domain.EnumElementProvider

infix fun <T : Any?> T.or(src: T): T = src ?: this

infix fun <T> T?.or(src: T?): T? where T : Enum<T>,
                                       T : EnumElementProvider.Key = src ?: this

infix fun <T : ComplexObject<T>> T?.updateBy(src: T?): T? {
    if (src == null) return this
    if (this == null) return src
    return this.updateBy(src)
}

interface ComplexObject<T> {
    infix fun updateBy(src: T): T
}

interface ComplexObjects<E, T : ComplexObjects<E, T>> : List<E> {

    operator fun plus(other: T): T

    operator fun plus(others: List<E>): T

    infix fun combineBy(src: T): T

    companion object {
        fun <E> merge(dst: List<E>, src: List<E>): List<E> = mutableSetOf<E>()
            .apply {
                addAll(dst)
                addAll(src)
            }
            .toList()
    }
}
