package com.procurement.orchestrator.infrastructure.client.web.access

import com.procurement.orchestrator.infrastructure.client.web.access.action.CheckAccessToTenderAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.CheckPersonsStructureAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetLotIdsAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetLotStateByIdsAction

object AccessCommands {

    object GetLotByIds : GetLotIdsAction()

    object GetLotStateByIds : GetLotStateByIdsAction()

    object CheckAccessToTender : CheckAccessToTenderAction()

    object CheckPersonsStructure : CheckPersonsStructureAction()
}
