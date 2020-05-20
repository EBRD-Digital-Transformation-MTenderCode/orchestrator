package com.procurement.orchestrator.domain.functional

fun <T> T?.asOption(): Option<T> = Option.fromNullable(this)
fun <T, C : Collection<T>> C.asOption(): Option<C> = Option.fromCollection(this)

sealed class Option<out T> {

    companion object {
        fun <T> pure(value: T): Option<T> = Some(value)
        fun <T> none(): Option<T> = None
        fun <T> fromNullable(value: T?): Option<T> = if (value != null) Some(value) else None
        fun <T, C : Collection<T>> fromCollection(values: C): Option<C> =
            if (values.isNotEmpty()) pure(values) else none()
    }

    abstract val get: T
    abstract val orNull: T?

    fun <E : Exception> orThrow(block: () -> E): T = when (this) {
        is Some -> get
        is None -> throw block()
    }

    abstract val isEmpty: Boolean
    abstract val nonEmpty: Boolean
    val isDefined: Boolean
        get() = !isEmpty

    inline infix fun <R> map(transform: (T) -> R): Option<R> = when (this) {
        is Some -> Some(transform(this.get))
        is None -> this
    }

    inline infix fun <R> bind(function: (T) -> Option<R>): Option<R> = when (this) {
        is Some -> function(this.get)
        is None -> this
    }

    inline infix fun apply(function: T.() -> Unit): Option<T> {
        when (this) {
            is Some -> get.function()
            is None -> Unit
        }
        return this
    }

    inline infix fun also(function: (T) -> Unit): Option<T> {
        when (this) {
            is Some -> function(get)
            is None -> Unit
        }
        return this
    }
}

class Some<out T>(value: T) : Option<T>() {
    override val get: T = value
    override val orNull: T = value
    override val isEmpty: Boolean = false
    override val nonEmpty: Boolean = !isEmpty
}

object None : Option<Nothing>() {
    override val get: Nothing
        get() = throw NoSuchElementException("Option do not contain value.")
    override val orNull: Nothing? = null
    override val isEmpty: Boolean = true
    override val nonEmpty: Boolean = !isEmpty
}
