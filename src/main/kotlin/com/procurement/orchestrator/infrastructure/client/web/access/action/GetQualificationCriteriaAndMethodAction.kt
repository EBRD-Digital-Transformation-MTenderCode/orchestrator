package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.tender.conversion.ConversionId
import com.procurement.orchestrator.domain.model.tender.conversion.ConversionsRelatesTo
import com.procurement.orchestrator.domain.model.tender.conversion.coefficient.CoefficientRate
import com.procurement.orchestrator.domain.model.tender.conversion.coefficient.CoefficientValue
import com.procurement.orchestrator.domain.model.tender.criteria.QualificationSystemMethod
import com.procurement.orchestrator.domain.model.tender.criteria.ReductionCriteria
import com.procurement.orchestrator.infrastructure.bind.conversion.coefficient.rate.CoefficientRateDeserializer
import com.procurement.orchestrator.infrastructure.bind.conversion.coefficient.rate.CoefficientRateSerializer
import com.procurement.orchestrator.infrastructure.bind.conversion.coefficient.value.CoefficientValueDeserializer
import com.procurement.orchestrator.infrastructure.bind.conversion.coefficient.value.CoefficientValueSerializer
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class GetQualificationCriteriaAndMethodAction : FunctionalAction<GetQualificationCriteriaAndMethodAction.Params, GetQualificationCriteriaAndMethodAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "getQualificationCriteriaAndMethod"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid
    )

    class Result(
        @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("conversions") @param:JsonProperty("conversions") val conversions: List<Conversion>?,

        @field:JsonProperty("qualificationSystemMethods") @param:JsonProperty("qualificationSystemMethods") val qualificationSystemMethods: List<QualificationSystemMethod>,
        @field:JsonProperty("reductionCriteria") @param:JsonProperty("reductionCriteria") val reductionCriteria: ReductionCriteria
    ) : Serializable {

        data class Conversion(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: ConversionId,
            @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: ConversionsRelatesTo,
            @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String,
            @field:JsonProperty("rationale") @param:JsonProperty("rationale") val rationale: String,
            @field:JsonProperty("coefficients") @param:JsonProperty("coefficients") val coefficients: List<Coefficient>,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String?
        ): Serializable {
            data class Coefficient(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

                @JsonDeserialize(using = CoefficientValueDeserializer::class)
                @JsonSerialize(using = CoefficientValueSerializer::class)
                @field:JsonProperty("value") @param:JsonProperty("value") val value: CoefficientValue,

                @JsonDeserialize(using = CoefficientRateDeserializer::class)
                @JsonSerialize(using = CoefficientRateSerializer::class)
                @field:JsonProperty("coefficient") @param:JsonProperty("coefficient") val coefficient: CoefficientRate
            ) : Serializable
        }
    }
}
