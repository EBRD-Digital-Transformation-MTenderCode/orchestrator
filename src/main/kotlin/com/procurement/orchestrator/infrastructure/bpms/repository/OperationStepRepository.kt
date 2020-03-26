package com.procurement.orchestrator.infrastructure.bpms.repository

import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.process.ProcessId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.infrastructure.bpms.model.TaskId
import java.time.LocalDateTime

data class OperationStep(
    val cpid: Cpid,
    val operationId: OperationId,
    val processId: ProcessId,
    val taskId: TaskId,
    val stepDate: LocalDateTime,
    val request: String,
    val response: String,
    val context: String
)

interface OperationStepRepository {
    fun save(step: OperationStep): Result<Boolean, Fail.Incident.Database.Access>
}
