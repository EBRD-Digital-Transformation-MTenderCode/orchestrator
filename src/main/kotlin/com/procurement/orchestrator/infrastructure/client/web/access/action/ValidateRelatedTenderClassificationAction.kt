package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.infrastructure.model.Version

abstract class ValidateRelatedTenderClassificationAction : ProceduralAction<ValidateRelatedTenderClassificationAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "validateClassification"

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") var ocid: Ocid,
        @field:JsonProperty("tender") @param:JsonProperty("tender") var tender: Tender
    ) {
        data class Tender(
            @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification
        ) {
            data class Classification(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String
            )
        }
    }
}
