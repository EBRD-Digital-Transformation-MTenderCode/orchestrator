package com.procurement.orchestrator.domain.model.lot

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.classification.Classification
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.updateBy
import com.procurement.orchestrator.domain.model.value.Value
import java.io.Serializable

data class Lot(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: LotId,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("internalId") @param:JsonProperty("internalId") val internalId: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("status") @param:JsonProperty("status") val status: LotStatus? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: LotStatusDetails? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("value") @param:JsonProperty("value") val value: Value? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("options") @param:JsonProperty("options") val options: Options = Options(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("recurrentProcurement") @param:JsonProperty("recurrentProcurement") val recurrentProcurement: RecurrentProcurements = RecurrentProcurements(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("renewals") @param:JsonProperty("renewals") val renewals: Renewals = Renewals(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("variants") @param:JsonProperty("variants") val variants: Variants = Variants(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("contractPeriod") @param:JsonProperty("contractPeriod") val contractPeriod: Period? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("placeOfPerformance") @param:JsonProperty("placeOfPerformance") val placeOfPerformance: PlaceOfPerformance? = null
) : IdentifiableObject<Lot>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Lot
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: Lot) = Lot(
        id = id,
        internalId = src.internalId or internalId,
        title = src.title or title,
        description = src.description or description,
        status = src.status or status,
        statusDetails = src.statusDetails or statusDetails,
        value = src.value or value,
        options = options combineBy src.options,
        recurrentProcurement = recurrentProcurement combineBy src.recurrentProcurement,
        renewals = renewals combineBy src.renewals,
        variants = variants combineBy src.variants,
        contractPeriod = contractPeriod updateBy src.contractPeriod,
        placeOfPerformance = placeOfPerformance updateBy src.placeOfPerformance
    )
}
