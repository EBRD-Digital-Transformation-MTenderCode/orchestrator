package com.procurement.orchestrator.infrastructure.client.web.contracting.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.party.Party
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class FindSupplierReferencesOfActivePacsAction : FunctionalAction<FindSupplierReferencesOfActivePacsAction.Params, FindSupplierReferencesOfActivePacsAction.Result> {

    override val version: Version = Version.parse("2.0.0")
    override val name: String = "findPacsByLotIds"
    override val target: Target<Result> = Target.single()

    data class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
    ) {
        data class Tender(
            @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>
        ) {
            data class Lot(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: LotId
            )
        }
    }

    data class Result(
        @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>
    ) : Serializable {
        data class Contract(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("status") @field:JsonProperty("status") val status: String,
            @param:JsonProperty("date") @field:JsonProperty("date") val date: String,
            @param:JsonProperty("relatedLots") @field:JsonProperty("relatedLots") val relatedLots: List<String>,
            @param:JsonProperty("suppliers") @field:JsonProperty("suppliers") val suppliers: List<Supplier>,
            @param:JsonProperty("awardId") @field:JsonProperty("awardId") val awardId: String,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("agreedMetrics ") @field:JsonProperty("agreedMetrics ") val agreedMetrics: List<AgreedMetric>?
        ) : Serializable {
            data class Supplier(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String
            ) : Serializable

            data class AgreedMetric(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("title") @field:JsonProperty("title") val title: String,
                @param:JsonProperty("observations") @field:JsonProperty("observations") val observations: List<Observation>
            ) : Serializable {
                data class Observation(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("notes") @field:JsonProperty("notes") val notes: String,
                    @param:JsonProperty("measure") @field:JsonProperty("measure") val measure: Any,
                    @param:JsonProperty("relatedRequirementId") @field:JsonProperty("relatedRequirementId") val relatedRequirementId: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("period") @field:JsonProperty("period") val period: Period?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("unit") @field:JsonProperty("unit") val unit: Unit?
                ) : Serializable {
                    data class Period(
                        @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: String,
                        @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: String
                    ) : Serializable

                    data class Unit(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                    ) : Serializable
                }
            }
        }
    }

    companion object {
        fun toDomain(supplier: Result.Contract.Supplier): Party =
            Party(
                id = supplier.id,
                name = supplier.name
            )
    }

}
