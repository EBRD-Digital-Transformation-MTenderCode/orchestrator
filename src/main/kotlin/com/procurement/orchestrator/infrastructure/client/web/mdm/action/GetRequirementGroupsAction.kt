package com.procurement.orchestrator.infrastructure.client.web.mdm.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.Phase
import com.procurement.orchestrator.domain.model.ProcurementMethod
import com.procurement.orchestrator.domain.model.tender.criteria.CriterionId
import java.io.Serializable

abstract class GetRequirementGroupsAction {

    class Params(
        @field:JsonProperty("lang") @param:JsonProperty("lang") val lang: String,
        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
        @field:JsonProperty("country") @param:JsonProperty("country") val country: String,
        @field:JsonProperty("pmd") @param:JsonProperty("pmd") val pmd: ProcurementMethod,
        @field:JsonProperty("phase") @param:JsonProperty("phase") val phase: Phase,
        @field:JsonProperty("criterionId") @param:JsonProperty("criterionId") val criterionId: CriterionId
    )

    sealed class Response {

        class Success(
            @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("data") @param:JsonProperty("data") val data: List<RequirementResponse>?
        ) : Response(), Serializable {

            data class RequirementResponse(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

                @field:JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String?
            ) : Serializable
        }
    }
}

object GetRequirementGroups {

    sealed class Result {
        data class Success(val requirementGroups: List<RequirementGroup>) : Result() {
            data class RequirementGroup(val id: String, val description: String?)
        }
    }
}