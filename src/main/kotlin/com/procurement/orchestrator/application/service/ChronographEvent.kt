package com.procurement.orchestrator.application.service

import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.PlatformId
import com.procurement.orchestrator.application.model.RequestId
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import java.time.LocalDateTime

data class ChronographEvent(
    val cpid: Cpid,
    val ocid: Ocid,
    val operationId: OperationId,
    val requestId: RequestId,
    val owner: Owner,
    val platformId: PlatformId,
    val processName: String,
    val timestamp: LocalDateTime
)
