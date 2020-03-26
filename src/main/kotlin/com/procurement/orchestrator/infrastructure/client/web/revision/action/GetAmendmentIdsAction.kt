package com.procurement.orchestrator.infrastructure.client.web.revision.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.amendment.AmendmentId
import com.procurement.orchestrator.domain.model.amendment.AmendmentRelatesTo
import com.procurement.orchestrator.domain.model.amendment.AmendmentStatus
import com.procurement.orchestrator.domain.model.amendment.AmendmentType
import com.procurement.orchestrator.infrastructure.client.web.Target
import java.io.Serializable

abstract class GetAmendmentIdsAction : FunctionalAction<GetAmendmentIdsAction.Params, GetAmendmentIdsAction.Result> {
    override val version: String = "2.0.0"
    override val name: String = "getAmendmentIds"
    override val target: Target<Result> =
        Target.Plural(typeRef = Result::class.java, defaultResult = { Result(emptyList<AmendmentId.Permanent>()) })

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("type") @param:JsonProperty("type") val type: AmendmentType?,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("status") @param:JsonProperty("status") val status: AmendmentStatus?,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: AmendmentRelatesTo?,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("relatedItems") @param:JsonProperty("relatedItems") val relatedItems: List<String> = emptyList()
    )

    class Result(values: List<AmendmentId.Permanent>) : List<AmendmentId> by values, Serializable
}
