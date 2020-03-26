package com.procurement.orchestrator.application.repository

import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.Cpid
import java.time.LocalDateTime

interface ProcessContextRepository {

    fun save(
        cpid: Cpid,
        timestamp: LocalDateTime,
        operationId: OperationId,
        context: String
    ): Result<Boolean, Fail.Incident.Database.Access>

    fun load(cpid: Cpid): Result<String?, Fail.Incident.Database>
}


