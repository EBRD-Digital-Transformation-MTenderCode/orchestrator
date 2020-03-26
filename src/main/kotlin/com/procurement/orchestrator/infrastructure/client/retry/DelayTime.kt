package com.procurement.orchestrator.infrastructure.client.retry

class DelayTime private constructor(
    private val start: Long,
    private val max: Long,
    private val step: TimeStep,
    val current: Long
) {
    companion object {
        private const val START_DEFAULT: Long = 100L
        private const val MAX_DEFAULT: Long = 60000L
        private val STEP_DEFAULT: TimeStep = TimeStep.Coefficient(1.2)

        operator fun invoke(start: Long = START_DEFAULT, max: Long = MAX_DEFAULT, step: TimeStep = STEP_DEFAULT) =
            DelayTime(start = start, max = max, step = step, current = start)
    }

    fun next(): DelayTime = DelayTime(
        start = start,
        max = max,
        step = step,
        current = step.next(current).coerceAtMost(max)
    )
}
