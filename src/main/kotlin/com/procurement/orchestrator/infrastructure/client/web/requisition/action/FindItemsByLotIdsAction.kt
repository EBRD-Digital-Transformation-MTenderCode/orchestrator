package com.procurement.orchestrator.infrastructure.client.web.requisition.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.identifier.IdentifierId
import com.procurement.orchestrator.domain.model.identifier.IdentifierScheme
import com.procurement.orchestrator.domain.model.item.ItemId
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.measure.Quantity
import com.procurement.orchestrator.domain.model.unit.UnitId
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class FindItemsByLotIdsAction : FunctionalAction<FindItemsByLotIdsAction.Params, FindItemsByLotIdsAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "findItemsByLotIds"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
    ) : Serializable {
        class Tender(
            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>?
        ) : Serializable {
            class Lot(@field:JsonProperty("id") @param:JsonProperty("id") val id: LotId) : Serializable
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
                @param:JsonProperty("unit") @field:JsonProperty("unit") val unit: Unit,
                @param:JsonProperty("relatedLot") @field:JsonProperty("relatedLot") val relatedLot: LotId
            ) : Serializable {
                data class Classification(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: IdentifierId,
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: IdentifierScheme,
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String
                ) : Serializable

                data class Unit(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: UnitId,
                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                ) : Serializable
            }
        }
    }
}
