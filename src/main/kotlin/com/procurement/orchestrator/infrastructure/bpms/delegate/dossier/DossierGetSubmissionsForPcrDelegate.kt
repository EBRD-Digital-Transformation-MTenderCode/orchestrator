package com.procurement.orchestrator.infrastructure.bpms.delegate.dossier

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.DossierClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.party.Parties
import com.procurement.orchestrator.domain.model.party.PartyRole
import com.procurement.orchestrator.domain.model.party.PartyRoles
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.FindSubmissionsForOpeningAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.convertToParty
import org.springframework.stereotype.Component

@Component
class DossierGetSubmissionsForPcrDelegate(
    logger: Logger,
    private val dossierClient: DossierClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, FindSubmissionsForOpeningAction.Result>(
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
    ): Result<Reply<FindSubmissionsForOpeningAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo
        return dossierClient.findSubmissionsForOpening(
            id = commandId,
            params = FindSubmissionsForOpeningAction.Params(
                cpid = processInfo.relatedProcess!!.cpid,
                ocid = processInfo.relatedProcess.ocid!!,
                pmd = processInfo.pmd,
                country = requestInfo.country,
                operationType = processInfo.operationType
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<FindSubmissionsForOpeningAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.none()

        val parties = data.flatMap { submission -> submission.candidates }
            .map { it.convertToParty() }
            .map { it.copy(roles = PartyRoles(PartyRole.INVITED_TENDERER)) }
            .let { Parties(it) }

        context.parties = context.parties updateBy parties

        return MaybeFail.none()
    }
}
