package com.procurement.orchestrator.domain.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.orchestrator.domain.EnumElementProvider

enum class ProcurementMethod(override val key: String) : EnumElementProvider.Key {
    MV("MV"),
    OT("OT"),
    RT("RT"),
    SV("SV"),
    DA("DA"),
    NP("NP"),
    FA("FA"),
    OP("OP"),
    TEST_OT("TEST_OT"),
    TEST_SV("TEST_SV"),
    TEST_RT("TEST_RT"),
    TEST_MV("TEST_MV"),
    TEST_DA("TEST_DA"),
    TEST_NP("TEST_NP"),
    TEST_FA("TEST_FA"),
    TEST_OP("TEST_OP");

    override fun toString(): String = key

    companion object : EnumElementProvider<ProcurementMethod>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = ProcurementMethod.orThrow(name)
    }
}
