package com.procurement.orchestrator.infrastructure.client.retry

sealed class TimeStep {
    abstract fun next(currentDelayTime: Long): Long

    class Coefficient(val value: Double) : TimeStep() {
        override fun next(currentDelayTime: Long): Long = (currentDelayTime * value).toLong()
    }

    class Linear(val value: Long) : TimeStep() {
        override fun next(currentDelayTime: Long): Long = currentDelayTime + value
    }

    object None : TimeStep() {
        override fun next(currentDelayTime: Long): Long = currentDelayTime
    }
}
