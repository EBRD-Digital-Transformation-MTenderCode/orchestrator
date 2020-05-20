package com.procurement.orchestrator.infrastructure.bpms.repository

import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.bpms.model.queue.QueueNoticeTask

interface NoticeQueueRepository {
    fun save(operationId: OperationId, task: QueueNoticeTask): Result<Boolean, Fail.Incident.Database.Access>

    fun load(operationId: OperationId): Result<List<QueueNoticeTask>, Fail.Incident.Database>
}
