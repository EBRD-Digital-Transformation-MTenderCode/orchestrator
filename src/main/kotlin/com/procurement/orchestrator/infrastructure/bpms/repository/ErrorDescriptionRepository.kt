package com.procurement.orchestrator.infrastructure.bpms.repository

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetErrorDescriptionsAction

interface ErrorDescriptionRepository {

    fun load(codes: List<String>, language: String): Result<GetErrorDescriptionsAction.Result, Fail.Incident.Database>
}
