package com.procurement.orchestrator.infrastructure.client.web.storage

import com.procurement.orchestrator.infrastructure.client.web.storage.action.CheckRegistrationAction
import com.procurement.orchestrator.infrastructure.client.web.storage.action.OpenAccessAction

object StorageCommands {

    object CheckRegistration : CheckRegistrationAction()
    object OpenAccess : OpenAccessAction()
}
