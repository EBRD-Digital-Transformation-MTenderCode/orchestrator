package com.procurement.orchestrator.domain.model

import com.procurement.orchestrator.domain.util.extension.getNewElements

infix fun <T : IdentifiableObject<T>> T?.updateBy(src: T?): T? {
    if (src == null) return this
    if (this == null) return src
    return this.updateBy(src)
}

interface IdentifiableObject<T> {

    infix fun updateBy(src: T): T
}

interface IdentifiableObjects<E : IdentifiableObject<E>, T : IdentifiableObjects<E, T>> : List<E> {

    operator fun plus(other: T): T

    operator fun plus(others: List<E>): T

    infix fun updateBy(src: T): T

    companion object {
        fun <E : IdentifiableObject<E>> update(dst: List<E>, src: List<E>): List<E> {
            val srcByIds: Map<E, E> = src.associateBy { it }
            val updatedElements: List<E> = dst.map { dstElement ->
                srcByIds[dstElement]
                    ?.let { src: E -> dstElement updateBy src }
                    ?: dstElement
            }
            val newElements: List<E> = getNewElements(received = srcByIds.keys, known = dst.toSet())
                .map { srcElement: E -> srcElement }
            return updatedElements + newElements
        }
    }
}
