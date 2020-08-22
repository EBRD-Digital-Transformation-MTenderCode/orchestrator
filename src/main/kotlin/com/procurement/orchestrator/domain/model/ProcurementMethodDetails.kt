package com.procurement.orchestrator.domain.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.orchestrator.domain.EnumElementProvider

enum class ProcurementMethodDetails(override val key: String) : EnumElementProvider.Key {
    CD("CD"),
    DA("DA"),
    DC("DC"),
    FA("FA"),
    GPA("GPA"),
    IP("IP"),
    MV("MV"),
    NP("NP"),
    OP("OP"),
    OT("OT"),
    RT("RT"),
    SV("SV"),
    TEST_CD("TEST_CD"),
    TEST_DA("TEST_DA"),
    TEST_DC("TEST_DC"),
    TEST_FA("TEST_FA"),
    TEST_GPA("TEST_GPA"),
    TEST_IP("TEST_IP"),
    TEST_MV("TEST_MV"),
    TEST_NP("TEST_NP"),
    TEST_OP("TEST_OP"),
    TEST_OT("TEST_OT"),
    TEST_RT("TEST_RT"),
    TEST_SV("TEST_SV");

    override fun toString(): String = key

    companion object : EnumElementProvider<ProcurementMethodDetails>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = ProcurementMethodDetails.orThrow(name)
    }
}
