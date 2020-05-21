package com.procurement.orchestrator.infrastructure.client.web.clarification.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.enquiry.EnquiryId
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class FindEnquiryIdsAction : FunctionalAction<FindEnquiryIdsAction.Params, FindEnquiryIdsAction.Result> {

    override val version: Version = Version.parse("2.0.0")
    override val name: String = "findEnquiryIds"
    override val target: Target<Result> = Target.plural()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("isAnswer") @get:JsonProperty("isAnswer") val isAnswer: Boolean?
    )

    class Result(enquiryIds: List<EnquiryId>) : List<EnquiryId> by enquiryIds, Serializable
}
