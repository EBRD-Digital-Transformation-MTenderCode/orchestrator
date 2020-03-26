package com.procurement.orchestrator.infrastructure.bpms.repository

import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import java.time.LocalDateTime

interface NoticeQueueRepository {
    fun save(task: NoticeTask): Result<Boolean, Fail.Incident.Database.Access>

    fun load(operationId: OperationId): Result<List<NoticeTask>, Fail.Incident.Database>
}

data class NoticeTask(
    val operationId: OperationId,
    val timestamp: LocalDateTime,
    val cpid: Cpid,
    val ocid: Ocid,
    val action: Action,
    val data: String
) {

    enum class Action(override val key: String) : EnumElementProvider.Key {

        CREATE_RECORD("createRecord"),
        UPDATE_RECORD("updateRecord");

        override fun toString(): String = key

        companion object : EnumElementProvider<Action>(info = info())
    }
}
