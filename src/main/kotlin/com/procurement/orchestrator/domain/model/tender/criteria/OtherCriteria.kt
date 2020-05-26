package com.procurement.orchestrator.domain.model.tender.criteria

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import com.procurement.orchestrator.domain.model.or
import java.io.Serializable

data class OtherCriteria(

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("reductionCriteria") @param:JsonProperty("reductionCriteria") val reductionCriteria: ReductionCriteria? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("qualificationSystemMethods") @param:JsonProperty("qualificationSystemMethods") val qualificationSystemMethods: QualificationSystemMethods = QualificationSystemMethods()

) : ComplexObject<OtherCriteria>,
    Serializable {

    override fun updateBy(src: OtherCriteria) = OtherCriteria(
        reductionCriteria = src.reductionCriteria or reductionCriteria,
        qualificationSystemMethods = src.qualificationSystemMethods or qualificationSystemMethods
    )
}
