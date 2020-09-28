package com.procurement.orchestrator.domain.model.tender

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class TenderStatusDetails(@JsonValue override val key: String) : EnumElementProvider.Key {

    AGGREGATED("aggregated"),
    AGGREGATION_PENDING("aggregationPending"),
    AUCTION("auction"),
    AWARDED_CONTRACT_PREPARATION("awardedContractPreparation"),
    AWARDED_STANDSTILL("awardedStandStill"),
    AWARDED_SUSPENDED("awardedSuspended"),
    AWARDING("awarding"),
    CANCELLATION("cancellation"),
    CLARIFICATION("clarification"),
    COMPLETE("complete"),
    EMPTY("empty"),
    EVALUATION("evaluation"),
    LACK_OF_QUALIFICATIONS("lackOfQualifications"),
    LACK_OF_SUBMISSIONS("lackOfSubmissions"),
    NEGOTIATION("negotiation"),
    PLANNED("planned"),
    PLANNING("planning"),
    QUALIFICATION("qualification"),
    QUALIFICATION_STAND_STILL("qualificationStandStill"),
    SUBMISSION("submission"),
    SUSPENDED("suspended"),
    TENDERING("tendering");

    override fun toString(): String = key

    companion object : EnumElementProvider<TenderStatusDetails>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
