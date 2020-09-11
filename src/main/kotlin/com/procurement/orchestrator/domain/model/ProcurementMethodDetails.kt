package com.procurement.orchestrator.domain.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.orchestrator.domain.EnumElementProvider

enum class ProcurementMethodDetails(override val key: String) : EnumElementProvider.Key {
    CD("CD"),
    CF("CF"),
    DA("DA"),
    DC("DC"),
    DCO("DCO"),
    FA("FA"),
    GPA("GPA"),
    IP("IP"),
    MC("MC"),
    MV("MV"),
    NP("NP"),
    OF("OF"),
    OP("OP"),
    OT("OT"),
    RFQ("RFQ"),
    RT("RT"),
    SV("SV"),
    TEST_CD("TEST_CD"),
    TEST_CF("TEST_CF"),
    TEST_DA("TEST_DA"),
    TEST_DC("TEST_DC"),
    TEST_DCO("TEST_DCO"),
    TEST_FA("TEST_FA"),
    TEST_GPA("TEST_GPA"),
    TEST_IP("TEST_IP"),
    TEST_MC("TEST_MC"),
    TEST_MV("TEST_MV"),
    TEST_NP("TEST_NP"),
    TEST_OF("TEST_OF"),
    TEST_OP("TEST_OP"),
    TEST_OT("TEST_OT"),
    TEST_RFQ("TEST_RFQ"),
    TEST_RT("TEST_RT"),
    TEST_SV("TEST_SV");

    override fun toString(): String = key

    companion object : EnumElementProvider<ProcurementMethodDetails>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = ProcurementMethodDetails.orThrow(name)
    }
}
