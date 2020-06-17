package com.procurement.orchestrator.domain.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.orchestrator.domain.EnumElementProvider

enum class ProcurementMethod(override val key: String) : EnumElementProvider.Key {
    DA("DA"),
    FA("FA"),
    GPA("GPA"),
    MV("MV"),
    NP("NP"),
    OP("OP"),
    OT("OT"),
    RT("RT"),
    SV("SV"),
    TEST_DA("TEST_DA"),
    TEST_FA("TEST_FA"),
    TEST_GPA("TEST_GPA"),
    TEST_MV("TEST_MV"),
    TEST_NP("TEST_NP"),
    TEST_OP("TEST_OP"),
    TEST_OT("TEST_OT"),
    TEST_RT("TEST_RT"),
    TEST_SV("TEST_SV");

    override fun toString(): String = key

    companion object : EnumElementProvider<ProcurementMethod>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = ProcurementMethod.orThrow(name)
    }
}
