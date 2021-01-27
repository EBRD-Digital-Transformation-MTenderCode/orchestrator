package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.item.ItemId
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.measure.Quantity
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class GetItemsByLotIdsAction : FunctionalAction<GetItemsByLotIdsAction.Params, GetItemsByLotIdsAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "getItemsByLotIds"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
    ) {
        data class Tender(
            @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>
        ) {
            data class Lot(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: LotId
            )
        }
    }

    class Result(
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender
    ) : Serializable {
        data class Tender(
            @param:JsonProperty("items") @field:JsonProperty("items") val items: List<Item>
        ) : Serializable {
            data class Item(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: ItemId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("internalId") @field:JsonProperty("internalId") val internalId: String?,

                @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                @param:JsonProperty("quantity") @field:JsonProperty("quantity") val quantity: Quantity,
                @param:JsonProperty("classification") @field:JsonProperty("classification") val classification: Classification,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("additionalClassifications") @field:JsonProperty("additionalClassifications") val additionalClassifications: List<AdditionalClassification>?,
                @param:JsonProperty("unit") @field:JsonProperty("unit") val unit: Unit,
                @param:JsonProperty("relatedLot") @field:JsonProperty("relatedLot") val relatedLot: LotId
            ) : Serializable {
                data class Classification(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String
                ) : Serializable

                data class AdditionalClassification(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String
                ) : Serializable

                data class Unit(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                ) : Serializable
            }
        }
    }
}
