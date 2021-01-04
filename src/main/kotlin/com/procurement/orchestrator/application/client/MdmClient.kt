package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichClassificationsAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichCountryAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichLocalityAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichRegionAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichUnitsAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetClassification
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetCountry
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetCriteria
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetCriteriaAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetErrorDescriptionsAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetLocality
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetRegion
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetRequirementGroups
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetRequirementGroupsAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetRequirements
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetRequirementsAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetStandardCriteria
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetStandardCriteriaAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetUnit

interface MdmClient {

    suspend fun getErrorDescription(
        id: CommandId,
        params: GetErrorDescriptionsAction.Params
    ): Result<Reply<GetErrorDescriptionsAction.Result>, Fail.Incident>

    suspend fun enrichCountry(params: EnrichCountryAction.Params): Result<GetCountry.Result, Fail.Incident>

    suspend fun enrichRegion(params: EnrichRegionAction.Params): Result<GetRegion.Result, Fail.Incident>

    suspend fun enrichLocality(params: EnrichLocalityAction.Params): Result<GetLocality.Result, Fail.Incident>

    suspend fun getRequirementGroups(params: GetRequirementGroupsAction.Params): Result<GetRequirementGroups.Result.Success, Fail.Incident>

    suspend fun getCriteria(params: GetCriteriaAction.Params): Result<GetCriteria.Result.Success, Fail.Incident>

    suspend fun getStandardCriteria(params: GetStandardCriteriaAction.Params): Result<GetStandardCriteria.Result.Success, Fail.Incident>

    suspend fun getRequirements(params: GetRequirementsAction.Params): Result<GetRequirements.Result.Success, Fail.Incident>

    suspend fun enrichClassifications(params: EnrichClassificationsAction.Params): Result<GetClassification.Result, Fail.Incident>

    suspend fun enrichUnits(params: EnrichUnitsAction.Params): Result<GetUnit.Result, Fail.Incident>

}
