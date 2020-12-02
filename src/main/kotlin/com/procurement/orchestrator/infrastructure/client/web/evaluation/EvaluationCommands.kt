package com.procurement.orchestrator.infrastructure.client.web.evaluation

import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.AddRequirementResponseAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckAccessToAwardAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckRelatedTendererAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CloseAwardPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CreateAwardAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CreateUnsuccessfulAwardsAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.GetAwardStateByIdsAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.StartAwardPeriodAction

object EvaluationCommands {

    object CheckAccessToAward : CheckAccessToAwardAction()

    object CheckRelatedTenderer : CheckRelatedTendererAction()

    object AddRequirementResponse : AddRequirementResponseAction()

    object GetAwardStateByIds : GetAwardStateByIdsAction()

    object CreateUnsuccessfulAwards : CreateUnsuccessfulAwardsAction()

    object CloseAwardPeriod : CloseAwardPeriodAction()

    object StartAwardPeriod: StartAwardPeriodAction()

    object CreateAward: CreateAwardAction()
}
