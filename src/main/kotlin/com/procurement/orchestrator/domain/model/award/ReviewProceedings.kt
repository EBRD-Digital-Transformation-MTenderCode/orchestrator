package com.procurement.orchestrator.domain.model.award

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class ReviewProceedings(
    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("buyerProcedureReview") @param:JsonProperty("buyerProcedureReview") val buyerProcedureReview: Boolean? = null,

    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("reviewBodyChallenge") @param:JsonProperty("reviewBodyChallenge") val reviewBodyChallenge: Boolean? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("legalProcedures") @param:JsonProperty("legalProcedures") val legalProcedures: List<LegalProceedings> = emptyList()
) : Serializable
