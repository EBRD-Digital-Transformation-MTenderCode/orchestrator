package com.procurement.orchestrator.domain.model.tender.conversion.coefficient

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.orchestrator.infrastructure.bind.conversion.coefficient.rate.CoefficientRateDeserializer
import com.procurement.orchestrator.infrastructure.bind.conversion.coefficient.rate.CoefficientRateSerializer
import com.procurement.orchestrator.infrastructure.bind.conversion.coefficient.value.CoefficientValueDeserializer
import com.procurement.orchestrator.infrastructure.bind.conversion.coefficient.value.CoefficientValueSerializer
import java.io.Serializable

data class Coefficient(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: CoefficientId,

    @JsonDeserialize(using = CoefficientValueDeserializer::class)
    @JsonSerialize(using = CoefficientValueSerializer::class)
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("value") @param:JsonProperty("value") val value: CoefficientValue? = null,

    @JsonDeserialize(using = CoefficientRateDeserializer::class)
    @JsonSerialize(using = CoefficientRateSerializer::class)
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("coefficient") @param:JsonProperty("coefficient") val coefficient: CoefficientRate? = null
) : Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Coefficient
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()
}
