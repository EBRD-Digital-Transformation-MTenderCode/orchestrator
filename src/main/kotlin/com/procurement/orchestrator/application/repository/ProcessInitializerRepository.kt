package com.procurement.orchestrator.application.repository

import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.process.ProcessId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.Cpid
import java.time.LocalDateTime

interface ProcessInitializerRepository {

    fun launchProcess(
        operationId: OperationId,
        timestamp: LocalDateTime,
        processId: ProcessId,
        cpid: Cpid
    ): Result<LaunchedProcessInfo, Fail.Incident.Database>

    fun isLaunchedProcess(operationId: OperationId): Result<Boolean, Fail.Incident.Database.Access>
}

class LaunchedProcessInfo(
    val wasLaunched: Boolean,
    val operationId: OperationId,
    val timestamp: LocalDateTime,
    val processId: ProcessId,
    val cpid: Cpid
)
