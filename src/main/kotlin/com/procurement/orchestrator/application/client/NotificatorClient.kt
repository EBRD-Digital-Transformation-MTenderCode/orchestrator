package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail

interface NotificatorClient<T> {

    suspend fun send(message: T): MaybeFail<Fail.Incident>
}
