package com.procurement.orchestrator.application.repository

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.Cpid

interface OldProcessContextRepository {

    fun save(cpid: Cpid, context: String): Result<Boolean, Fail.Incident.Database.Access>

    fun load(cpid: Cpid): Result<String?, Fail.Incident.Database>
}


