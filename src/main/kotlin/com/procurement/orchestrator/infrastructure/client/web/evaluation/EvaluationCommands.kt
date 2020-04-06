package com.procurement.orchestrator.infrastructure.client.web.evaluation

import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckAccessToAwardAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckRelatedTendererAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CreateRequirementResponseAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.GetAwardStateByIdsAction

object EvaluationCommands {

    object CheckAccessToAward : CheckAccessToAwardAction()

    object CheckRelatedTenderer : CheckRelatedTendererAction()

    object CreateRequirementResponse : CreateRequirementResponseAction()

    object GetAwardStateByIds : GetAwardStateByIdsAction()
}
