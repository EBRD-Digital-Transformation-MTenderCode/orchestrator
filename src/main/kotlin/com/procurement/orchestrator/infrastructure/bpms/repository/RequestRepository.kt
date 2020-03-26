package com.procurement.orchestrator.infrastructure.bpms.repository

import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.PlatformId
import com.procurement.orchestrator.application.model.RequestId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import java.time.LocalDateTime

interface RequestRepository {
    fun save(requestRecord: RequestRecord): Result<Boolean, Fail.Incident.Database.Access>

    fun load(
        operationId: OperationId,
        timestamp: LocalDateTime,
        requestId: RequestId
    ): Result<RequestRecord?, Fail.Incident.Database>
}

data class RequestRecord(
    val operationId: OperationId,
    val timestamp: LocalDateTime,
    val requestId: RequestId,
    val platformId: PlatformId,
    val context: String,
    val payload: String
)
