package com.procurement.orchestrator.infrastructure.client.retry

sealed class Attempts {
    abstract val hasNext: Boolean
    abstract val nonNext: Boolean
    abstract fun next(): Attempts

    object Unlimited : Attempts() {
        override val hasNext: Boolean = true
        override val nonNext: Boolean = false
        override fun next(): Attempts = this
    }

    class Limited(private val residue: Int = COUNT_DEFAULT) : Attempts() {
        companion object {
            private const val COUNT_DEFAULT = 3
        }

        override val hasNext: Boolean
            get() = residue != 0

        override val nonNext: Boolean
            get() = !hasNext

        override fun next(): Attempts = Limited(residue = (residue - 1).coerceAtLeast(0))
    }
}
