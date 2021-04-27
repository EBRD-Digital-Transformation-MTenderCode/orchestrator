package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.process.master.data.Buyer
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class GetBuyersOwnersAction : FunctionalAction<GetBuyersOwnersAction.Params, GetBuyersOwnersAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "getBuyersOwners"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid
    )

    class Result(
        @param:JsonProperty("buyers") @field:JsonProperty("buyers") val buyers: List<Buyer>
    ) : Serializable {

        data class Buyer(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
            @param:JsonProperty("owner") @field:JsonProperty("owner") val owner: String
        ) : Serializable
    }
}

fun GetBuyersOwnersAction.Result.Buyer.toDomain() =
    Buyer(
        id = id,
        name = name,
        owner = owner
    )

