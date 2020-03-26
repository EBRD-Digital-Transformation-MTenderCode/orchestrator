package com.procurement.orchestrator.domain.model.item

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.orchestrator.domain.model.address.Address
import com.procurement.orchestrator.domain.model.classification.Classification
import com.procurement.orchestrator.domain.model.item.unit.ItemUnit
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.measure.Quantity
import com.procurement.orchestrator.infrastructure.bind.measure.quantity.QuantityDeserializer
import com.procurement.orchestrator.infrastructure.bind.measure.quantity.QuantitySerializer
import java.io.Serializable

data class Item(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: ItemId,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("internalId") @param:JsonProperty("internalId") val internalId: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("additionalClassifications") @param:JsonProperty("additionalClassifications") val additionalClassifications: List<Classification> = emptyList(),

    @param:JsonDeserialize(using = QuantityDeserializer::class)
    @field:JsonSerialize(using = QuantitySerializer::class)
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("quantity") @param:JsonProperty("quantity") val quantity: Quantity? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("unit") @param:JsonProperty("unit") val unit: ItemUnit? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("deliveryAddress") @param:JsonProperty("deliveryAddress") val deliveryAddress: Address? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatedLot") @param:JsonProperty("relatedLot") val relatedLot: LotId? = null
) : Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Item
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()
}
