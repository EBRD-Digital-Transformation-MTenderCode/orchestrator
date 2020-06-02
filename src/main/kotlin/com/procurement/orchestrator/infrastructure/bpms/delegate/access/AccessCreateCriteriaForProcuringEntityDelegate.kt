package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.tender.criteria.Criteria
import com.procurement.orchestrator.domain.model.tender.criteria.Criterion
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.Requirement
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroup
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroups
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.Requirements
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.AccessCommands
import com.procurement.orchestrator.infrastructure.client.web.access.action.CreateCriteriaForProcuringEntityAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.CreateCriteriaForProcuringEntityAction.Params
import org.springframework.stereotype.Component

@Component
class AccessCreateCriteriaForProcuringEntityDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, CreateCriteriaForProcuringEntityAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<CreateCriteriaForProcuringEntityAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val tender = context.tryGetTender()
            .orForwardFail { error -> return error }

        val criteriaFromContext = tender.criteria
            .map { it.convertToRequestEntity() }

        return accessClient.createCriteriaForProcuringEntity(
            id = commandId,
            params = Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                criteria = criteriaFromContext
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<CreateCriteriaForProcuringEntityAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = "eAccess",
                    action = AccessCommands.CreateCriteriaForProcuringEntity
                )
            )

        val tender = context.tryGetTender()
            .orReturnFail { return MaybeFail.fail(it) }

        val createdCriteria = data.map { it.convertToGlobalContextEntity() }
        context.tender = tender.copy(criteria = Criteria(createdCriteria))

        return MaybeFail.none()
    }

    private fun CreateCriteriaForProcuringEntityAction.Result.Criterion.convertToGlobalContextEntity(): Criterion {

        val requirementGroups = this.requirementGroups
            .map { it.convertToGlobalContextEntity() }

        return Criterion(
            id                = this.id,
            description       = this.description,
            source            = this.source,
            title             = this.title,
            requirementGroups = RequirementGroups(requirementGroups)
        )
    }

    private fun CreateCriteriaForProcuringEntityAction.Result.Criterion.RequirementGroup.convertToGlobalContextEntity() = RequirementGroup(
        id           = this.id,
        description  = this.description,
        requirements = Requirements(this.requirements)
    )

    private fun Criterion.convertToRequestEntity(): Params.Criterion {
        val requirementGroups = this.requirementGroups
            .map { it.convertToRequestEntity() }

        return Params.Criterion(
            id                = this.id,
            title             = this.title!!,
            description       = this.description,
            requirementGroups = requirementGroups
        )
    }

    private fun RequirementGroup.convertToRequestEntity(): Params.Criterion.RequirementGroup {
        val requirements = this.requirements
            .map { it.convertToRequestEntity() }

        return Params.Criterion.RequirementGroup(
            id           = this.id,
            description  = this.description,
            requirements = requirements
        )
    }

    private fun Requirement.convertToRequestEntity(): Params.Criterion.RequirementGroup.Requirement =
        Params.Criterion.RequirementGroup.Requirement(
            id          = this.id,
            title       = this.title,
            description = this.description
        )
}
