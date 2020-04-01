package com.procurement.orchestrator.infrastructure.client.web.evaluation

import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckAccessToAwardAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckRelatedTendererAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.GetAwardStateByIdsAction

object EvaluateCommands {

    object CheckAccessToAward : CheckAccessToAwardAction()

    object CheckRelatedTenderer : CheckRelatedTendererAction()

    object GetAwardStateByIds : GetAwardStateByIdsAction()
}
