package com.procurement.orchestrator.application.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class Stage(@JsonValue override val key: String, private val weight: Int) : EnumElementProvider.Key {

    AC("AC", weight = 60),
    AP("AP", weight = 30),
    EI("EI", weight = 10),
    EV("EV", weight = 40),
    FE("FE", weight = 40),
    FS("FS", weight = 20),
    NP("NP", weight = 40),
    PC("PC", weight = 50),
    PN("PN", weight = 30),
    RQ("RQ", weight = 40),
    TP("TP", weight = 40),
    ;

    override fun toString(): String = key

    companion object : EnumElementProvider<Stage>(info = info()), Comparator<Stage> {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)

        override fun compare(current: Stage, other: Stage): Int = current.weight.compareTo(other.weight)
    }
}
