package com.procurement.orchestrator.application.model.context.members

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.PlatformId
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.amendment.AmendmentId
import com.procurement.orchestrator.domain.model.award.AwardId
import com.procurement.orchestrator.domain.model.bid.BidId
import com.procurement.orchestrator.domain.model.contract.ContractId
import com.procurement.orchestrator.domain.model.qualification.QualificationId
import com.procurement.orchestrator.domain.model.submission.SubmissionId
import java.io.Serializable

class Outcomes(values: MutableMap<PlatformId, Details> = mutableMapOf()) :
    MutableMap<PlatformId, Outcomes.Details> by values, Serializable {

    data class Details(
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("amendments") @param:JsonProperty("amendments") val amendments: List<Amendment> = emptyList(),

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("awards") @param:JsonProperty("awards") val awards: List<Award> = emptyList(),

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("submissions") @param:JsonProperty("submissions") val submissions: List<Submission> = emptyList(),

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("qualifications") @param:JsonProperty("qualifications") val qualifications: List<Qualification> = emptyList(),

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("pc") @param:JsonProperty("pc") val pcr: List<PreAwardCatalogRequest> = emptyList(),

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("bids") @param:JsonProperty("bids") val bids: Bids? = null,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("contracts") @param:JsonProperty("contracts") val contracts: List<Contract> = emptyList()

    ) : Serializable {

        data class Amendment(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: AmendmentId,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("token") @param:JsonProperty("token") val token: Token?
        ) : Serializable

        data class Award(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: AwardId,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("token") @param:JsonProperty("token") val token: Token? = null
        ) : Serializable

        data class Submission(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: SubmissionId,
            @field:JsonProperty("token") @param:JsonProperty("token") val token: Token
        ) : Serializable

        data class Qualification(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: QualificationId,
            @field:JsonProperty("token") @param:JsonProperty("token") val token: Token
        ) : Serializable

        data class PreAwardCatalogRequest(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: Ocid,
            @field:JsonProperty("token") @param:JsonProperty("token") val token: Token
        ) : Serializable

        data class Bids(
            @field:JsonProperty("details") @param:JsonProperty("details") val details: List<Details>
        ) : Serializable {
            data class Details(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: BidId,
                @field:JsonProperty("token") @param:JsonProperty("token") val token: Token
            ) : Serializable
        }

        data class Contract(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: ContractId,
            @field:JsonProperty("token") @param:JsonProperty("token") val token: Token
        ) : Serializable
    }
}
