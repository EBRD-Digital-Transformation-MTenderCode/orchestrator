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
import com.procurement.orchestrator.domain.model.process.master.data.Candidate
import com.procurement.orchestrator.domain.model.process.master.data.Candidates
import com.procurement.orchestrator.domain.model.process.master.data.Dossier
import com.procurement.orchestrator.domain.model.process.master.data.ProcessMasterData
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.dossier.DossierCommands
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.GetInvitedCandidatesOwnersAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class DossierGetInvitedCandidatesOwnersDelegate(
    logger: Logger,
    private val dossierClient: DossierClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, GetInvitedCandidatesOwnersAction.Result>(
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
    ): Result<Reply<GetInvitedCandidatesOwnersAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        return dossierClient.getInvitedCandidatesOwners(
            id = commandId,
            params = GetInvitedCandidatesOwnersAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<GetInvitedCandidatesOwnersAction.Result>
    ): MaybeFail<Fail.Incident> {
        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.DOSSIER,
                    action = DossierCommands.GetInvitedCandidatesOwners
                )
            )
        val candidates = data.candidates

        context.processMasterData = ProcessMasterData(dossier = Dossier(candidates = candidates as Candidates))

        return MaybeFail.none()
    }
}