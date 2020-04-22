package com.procurement.orchestrator.domain.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.application.model.Stage
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.extension.date.toMilliseconds
import java.io.Serializable
import java.time.LocalDateTime

sealed class Ocid(protected val value: String) : Comparable<Ocid>, Serializable {

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is Ocid
                && this.value == other.value
        else
            true
    }

    override fun hashCode(): Int = value.hashCode()

    @JsonValue
    override fun toString(): String = value

    class MultiStage private constructor(value: String) : Ocid(value = value) {

        override fun compareTo(other: Ocid): Int = when (other) {
            is MultiStage -> value.compareTo(other.value)
            is SingleStage -> -1
        }

        companion object {
            private val regex = "^[a-z]{4}-[a-z0-9]{6}-[A-Z]{2}-[0-9]{13}\$".toRegex()

            val pattern: String
                get() = regex.pattern

            fun tryCreateOrNull(value: String): Ocid? = if (value.matches(regex)) MultiStage(value = value) else null

            fun generate(cpid: Cpid): Ocid = MultiStage(cpid.toString())
        }
    }

    class SingleStage private constructor(value: String, val stage: Stage) : Ocid(value = value) {

        override fun compareTo(other: Ocid): Int = when (other) {
            is MultiStage -> 1
            is SingleStage -> Stage.compare(current = stage, other = other.stage)
        }

        companion object {
            private const val STAGE_POSITION = 4
            private val STAGES: String
                get() = Stage.allowedElements.keysAsStrings()
                    .joinToString(separator = "|", prefix = "(", postfix = ")") { it.toUpperCase() }

            private val regex = "^[a-z]{4}-[a-z0-9]{6}-[A-Z]{2}-[0-9]{13}-$STAGES-[0-9]{13}\$".toRegex()

            val pattern: String
                get() = regex.pattern

            fun tryCreateOrNull(value: String): Ocid? =
                if (value.matches(regex)) {
                    val stage = Stage.orNull(value.split("-")[STAGE_POSITION])!!
                    SingleStage(value = value, stage = stage)
                } else
                    null

            fun generate(cpid: Cpid, stage: Stage, timestamp: LocalDateTime): Ocid =
                SingleStage("$cpid-$stage-${timestamp.toMilliseconds()}", stage)
        }
    }

    companion object {

        @JvmStatic
        @JsonCreator
        fun parse(value: String): Ocid? = SingleStage.tryCreateOrNull(value) ?: SingleStage.tryCreateOrNull(value)
    }
}
