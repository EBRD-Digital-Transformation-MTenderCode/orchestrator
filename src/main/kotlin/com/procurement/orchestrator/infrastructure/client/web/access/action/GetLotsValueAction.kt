package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.lot.Lot
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.measure.Amount
import com.procurement.orchestrator.domain.model.value.Value
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class GetLotsValueAction : FunctionalAction<GetLotsValueAction.Params, GetLotsValueAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "getLotsValue"
    override val target: Target<Result> = Target.single()

    class Params(
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

    class Result(
        @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
    ) : Serializable {
        data class Tender(
            @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>
        ) : Serializable {
            data class Lot(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: LotId,
                @field:JsonProperty("value") @param:JsonProperty("value") val value: Value
            ) : Serializable {
                data class Value(
                    @field:JsonProperty("amount") @param:JsonProperty("amount") val amount: Amount,
                    @field:JsonProperty("currency") @param:JsonProperty("currency") val currency: String
                ) : Serializable
            }
        }
    }
}

fun GetLotsValueAction.Result.Tender.Lot.toDomain() = Lot(
    id = id,
    value = value.let { value ->
        Value(
            amount = value.amount,
            currency = value.currency
        )
    }
)
