package com.procurement.orchestrator.application.service.confirmation

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.PlatformId
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.amendment.AmendmentId

object ConfirmationAmendment {

    class Request(
        val operationId: OperationId,
        val platformId: PlatformId,
        val context: Context
    ) {
        class Context(
            @field:JsonProperty("token") @param:JsonProperty("token") val token: Token,
            @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: Owner,
            @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
            @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
            @field:JsonProperty("amendmentId") @param:JsonProperty("amendmentId") val amendmentId: AmendmentId
        )
    }
}
