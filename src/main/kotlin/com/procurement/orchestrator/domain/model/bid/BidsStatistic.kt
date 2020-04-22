package com.procurement.orchestrator.domain.model.bid

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject

import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.or
import java.io.Serializable
import java.time.LocalDateTime

data class BidsStatistic(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: BidsStatisticId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("measure") @param:JsonProperty("measure") val measure: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime? = null,

    //TODO
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("value") @param:JsonProperty("value") val value: Double? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("notes") @param:JsonProperty("notes") val notes: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatedLot") @param:JsonProperty("relatedLot") val relatedLot: LotId? = null
) : IdentifiableObject<BidsStatistic>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is BidsStatistic
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: BidsStatistic) = BidsStatistic(
        id = id,
        measure = src.measure or measure,
        date = src.date or date,
        value = src.value or value,
        notes = src.notes or notes,
        relatedLot = src.relatedLot or relatedLot
    )
}
