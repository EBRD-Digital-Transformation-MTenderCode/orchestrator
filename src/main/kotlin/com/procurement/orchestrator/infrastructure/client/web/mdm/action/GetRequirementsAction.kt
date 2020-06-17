package com.procurement.orchestrator.infrastructure.client.web.mdm.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.Phase
import com.procurement.orchestrator.domain.model.ProcurementMethod
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroupId
import java.io.Serializable

abstract class GetRequirementsAction {

    class Params(
        @field:JsonProperty("lang") @param:JsonProperty("lang") val lang: String,
        @field:JsonProperty("country") @param:JsonProperty("country") val country: String,
        @field:JsonProperty("pmd") @param:JsonProperty("pmd") val pmd: ProcurementMethod,
        @field:JsonProperty("phase") @param:JsonProperty("phase") val phase: Phase,
        @field:JsonProperty("requirementGroupId") @param:JsonProperty("requirementGroupId") val requirementGroupId: RequirementGroupId
    )

    sealed class Response {

        class Success(
            @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("data") @param:JsonProperty("data") val data: List<RequirementResponse>?
        ) : Response(), Serializable {

            data class RequirementResponse(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

                @field:JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String?
            ) : Serializable
        }
    }
}

object GetRequirements {

    sealed class Result {
        data class Success(val requirements: List<Requirement>) : Result() {
            data class Requirement(val id: String, val title: String, val description: String?)
        }
    }
}