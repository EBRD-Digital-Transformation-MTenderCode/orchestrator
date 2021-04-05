package com.procurement.orchestrator.infrastructure.client.web.requisition.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.classification.Classification
import com.procurement.orchestrator.domain.model.item.Item
import com.procurement.orchestrator.domain.model.item.ItemId
import com.procurement.orchestrator.domain.model.lot.Lot
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.measure.Quantity
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.domain.model.unit.Unit
import com.procurement.orchestrator.infrastructure.model.Version

abstract class CheckItemsDataForRfqAction : ProceduralAction<CheckItemsDataForRfqAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "checkItemsDataForRfq"

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
    ) {
        data class Tender(
            @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>,
            @field:JsonProperty("items") @param:JsonProperty("items") val items: List<Item>
        ) { companion object {}

            data class Lot(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: LotId
            )

            data class Item(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: ItemId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("quantity") @param:JsonProperty("quantity") val quantity: Quantity?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("unit") @param:JsonProperty("unit") val unit: Unit?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("relatedLot") @param:JsonProperty("relatedLot") val relatedLot: LotId?
            ) {
                data class Unit(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String
                )

                data class Classification(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String
                )
            }
        }
    }
}

fun CheckItemsDataForRfqAction.Params.Tender.Companion.fromDomain(tender: Tender): CheckItemsDataForRfqAction.Params.Tender {
    return CheckItemsDataForRfqAction.Params.Tender(
        lots = tender.lots.map { lot -> lot.toParams() },
        items = tender.items.map { item -> item.toParams() }
    )
}

private fun Lot.toParams() = CheckItemsDataForRfqAction.Params.Tender.Lot(id = id)

private fun Item.toParams() =
    CheckItemsDataForRfqAction.Params.Tender.Item(
        id = id,
        classification = classification?.toParams(),
        quantity = quantity,
        unit = unit?.toParams(),
        relatedLot = relatedLot
    )

private fun Classification.toParams() =
    CheckItemsDataForRfqAction.Params.Tender.Item.Classification(id = id, scheme = scheme)

private fun Unit.toParams() =
    CheckItemsDataForRfqAction.Params.Tender.Item.Unit(id = id)