package com.procurement.orchestrator.domain.model.award

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import com.procurement.orchestrator.domain.model.or
import java.io.Serializable

data class ReviewProceedings(
    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("buyerProcedureReview") @param:JsonProperty("buyerProcedureReview") val buyerProcedureReview: Boolean? = null,

    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("reviewBodyChallenge") @param:JsonProperty("reviewBodyChallenge") val reviewBodyChallenge: Boolean? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("legalProcedures") @param:JsonProperty("legalProcedures") val legalProcedures: LegalProceedings = LegalProceedings()
) : ComplexObject<ReviewProceedings>, Serializable {

    override fun updateBy(src: ReviewProceedings) = ReviewProceedings(
        buyerProcedureReview = src.buyerProcedureReview or buyerProcedureReview,
        reviewBodyChallenge = src.reviewBodyChallenge or reviewBodyChallenge,
        legalProcedures = legalProcedures updateBy src.legalProcedures
    )
}
