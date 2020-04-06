package com.procurement.orchestrator.application

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.application.model.process.ProcessId
import java.io.Serializable
import java.util.*

class CommandId private constructor(private val value: String) : Serializable {

    operator fun plus(salt: String): CommandId = CommandId("$value:$salt".asUUID().toString())

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is CommandId
                && this.value == other.value
        else
            true
    }

    override fun hashCode(): Int = value.hashCode()

    @JsonValue
    override fun toString(): String = value

    companion object {
        fun generate(processId: ProcessId, activityId: String): CommandId =
            CommandId("$processId:$activityId".asUUID().toString())

        private fun String.asUUID(): UUID = UUID.nameUUIDFromBytes(this.toByteArray())
    }
}
