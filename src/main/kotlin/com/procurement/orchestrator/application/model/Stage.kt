package com.procurement.orchestrator.application.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class Stage(@JsonValue override val key: String, private val weight: Int) : EnumElementProvider.Key {

    EI("EI", weight = 10),
    FS("FS", weight = 20),
    PN("PN", weight = 30),
    EV("EV", weight = 40),
    NP("NP", weight = 40),
    AC("AC", weight = 50);

    override fun toString(): String = key

    companion object : EnumElementProvider<Stage>(info = info()), Comparator<Stage> {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)

        override fun compare(current: Stage, other: Stage): Int = current.weight.compareTo(other.weight)
    }
}
