package com.procurement.orchestrator.domain.util.extension

import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asSuccess

inline fun <T, V> Collection<T>.isUnique(selector: (T) -> V): Boolean {
    val unique = HashSet<V>()
    forEach { item ->
        if (!unique.add(selector(item))) return false
    }
    return true
}

inline fun <T, V> Collection<T>.getDuplicate(selector: (T) -> V): T? {
    val unique = HashSet<V>()
    forEach { item ->
        if (!unique.add(selector(item))) return item
    }
    return null
}

inline fun <T, V> Collection<T>.toSetBy(selector: (T) -> V): Set<V> {
    val collections = LinkedHashSet<V>()
    forEach {
        collections.add(selector(it))
    }
    return collections
}

inline fun <reified T> T.asList() = listOf(this)

inline fun <T, R, E> List<T>.mapToResult(transform: (T) -> Result<R, E>): Result<List<R>, E> = mutableListOf<R>()
    .apply {
        for (element in this@mapToResult) {
            when (val result = transform(element)) {
                is Result.Success -> this.add(result.get)
                is Result.Failure -> return result
            }
        }
    }
    .asSuccess()

inline fun <T, R, E> List<T>?.mapToNullableResult(transform: (T) -> Result<R, E>): Result<List<R>?, E> = this
    ?.mapToResult(transform)
    ?: success(null)

inline fun <T, R, E> List<T>?.mapToOptionalResult(transform: (T) -> Result<R, E>): Result<Option<List<R>>, E> = this
    ?.mapToResult(transform)
    ?.map { Option.pure(it) }
    ?: success(Option.none())

fun <T> getMissingElements(received: Iterable<T>, known: Iterable<T>): Set<T> =
    known.asSet().subtract(received.asSet())

fun <T> getUnknownElements(received: Iterable<T>, known: Iterable<T>) =
    getNewElements(received = received, known = known)

fun <T> getNewElements(received: Iterable<T>, known: Iterable<T>): Set<T> =
    received.asSet().subtract(known.asSet())

fun <T> getElementsForUpdate(received: Iterable<T>, known: Iterable<T>): Set<T> =
    known.asSet().intersect(received.asSet())

private fun <T> Iterable<T>.asSet(): Set<T> = when (this) {
    is Set -> this
    else -> this.toSet()
}
