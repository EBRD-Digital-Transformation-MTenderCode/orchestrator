package com.procurement.orchestrator.infrastructure.client.web.requisition.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.bid.BidId
import com.procurement.orchestrator.domain.model.item.ItemId
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.requirement.RequirementId
import com.procurement.orchestrator.domain.model.requirement.RequirementResponseValue
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId
import com.procurement.orchestrator.infrastructure.model.Version
import java.time.LocalDateTime

abstract class ValidateRequirementResponsesAction : ProceduralAction<ValidateRequirementResponsesAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "validateRequirementResponses"

    class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender?,

        @param:JsonProperty("bids") @field:JsonProperty("bids") val bids: Bids
    ) {
        data class Tender(
            @param:JsonProperty("procurementMethodModalities") @field:JsonProperty("procurementMethodModalities") val procurementMethodModalities: List<String>
        )

        data class Bids(
            @param:JsonProperty("details") @field:JsonProperty("details") val details: List<Detail>
        ) {
            data class Detail(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: BidId,
                @param:JsonProperty("relatedLots") @field:JsonProperty("relatedLots") val relatedLots: List<LotId>,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("items") @field:JsonProperty("items") val items: List<Item>?,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("requirementResponses") @field:JsonProperty("requirementResponses") val requirementResponses: List<RequirementResponse>?
            ) {
                data class Item(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: ItemId
                )

                data class RequirementResponse(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementResponseId,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("value") @field:JsonProperty("value") val value: RequirementResponseValue?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("requirement") @field:JsonProperty("requirement") val requirement: Requirement?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("period") @field:JsonProperty("period") val period: Period?
                ) {
                    data class Requirement(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementId
                    )

                    data class Period(
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime?
                    )
                }
            }
        }
    }
}
