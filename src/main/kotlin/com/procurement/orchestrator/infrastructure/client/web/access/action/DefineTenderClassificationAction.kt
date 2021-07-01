package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.item.ItemId
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class DefineTenderClassificationAction :
    FunctionalAction<DefineTenderClassificationAction.Params, DefineTenderClassificationAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "defineTenderClassification"
    override val target: Target<Result> = Target.single()

    class Params(
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender,
        @param:JsonProperty("relatedCpid") @field:JsonProperty("relatedCpid") val relatedCpid: Cpid,
        @param:JsonProperty("relatedOcid") @field:JsonProperty("relatedOcid") val relatedOcid: Ocid
    ) {
        data class Tender(
            @param:JsonProperty("items") @field:JsonProperty("items") val items: List<Item>
        ) {
            data class Item(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: ItemId,
                @param:JsonProperty("classification") @field:JsonProperty("classification") val classification: Classification
            ) {
                data class Classification(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String
                )
            }
        }
    }

    class Result(
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender
    ) : Serializable {

        data class Tender(
            @param:JsonProperty("classification") @field:JsonProperty("classification") val classification: Classification
        ) : Serializable {

            data class Classification(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String
            ) : Serializable
        }
    }
}
