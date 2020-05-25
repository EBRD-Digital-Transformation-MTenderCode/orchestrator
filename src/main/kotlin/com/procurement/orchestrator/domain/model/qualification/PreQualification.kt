package com.procurement.orchestrator.domain.model.qualification

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.updateBy
import java.io.Serializable

data class PreQualification(

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("period") @param:JsonProperty("period") val period: Period? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("qualificationPeriod") @param:JsonProperty("qualificationPeriod") val qualificationPeriod: Period? = null
) : ComplexObject<PreQualification>, Serializable {

    override fun updateBy(src: PreQualification): PreQualification = PreQualification(
        period = period updateBy src.period,
        qualificationPeriod = qualificationPeriod updateBy src.qualificationPeriod
    )
}
