package com.procurement.orchestrator.infrastructure.bpms.delegate

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.orchestrator.domain.EnumElementProvider
import java.time.LocalDateTime

class BpeIncident(
    val id: String,
    val date: LocalDateTime,
    val level: Level,
    val service: Service,
    val details: List<Detail>
) {

    data class Service(val id: String, val name: String, val version: String)
    data class Detail(val code: String, val description: String, val metadata: String = "")

    enum class Level(override val key: String) : EnumElementProvider.Key {
        ERROR("error"),
        WARNING("warning"),
        INFO("info");

        companion object : EnumElementProvider<Level>(info = info()) {

            @JvmStatic
            @JsonCreator
            fun creator(name: String) = orThrow(name)
        }
    }
}
