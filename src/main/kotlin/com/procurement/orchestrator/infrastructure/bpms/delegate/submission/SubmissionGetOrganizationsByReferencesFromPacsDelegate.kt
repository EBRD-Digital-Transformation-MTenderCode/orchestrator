package com.procurement.orchestrator.infrastructure.bpms.delegate.submission

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.SubmissionClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.party.Parties
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.submission.SubmissionCommands
import com.procurement.orchestrator.infrastructure.client.web.submission.action.GetOrganizationsByReferencesFromPacsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.toDomain
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class SubmissionGetOrganizationsByReferencesFromPacsDelegate(
    logger: Logger,
    private val submissionClient: SubmissionClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, GetOrganizationsByReferencesFromPacsAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {
    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        Result.success(Unit)

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<GetOrganizationsByReferencesFromPacsAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        val parties = context.parties

        return submissionClient.getOrganizationsByReferencesFromPacs(
            id = commandId,
            params = GetOrganizationsByReferencesFromPacsAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                parties = parties.map { party ->
                    GetOrganizationsByReferencesFromPacsAction.Params.Party(id = party.id)
                }
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<GetOrganizationsByReferencesFromPacsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.SUBMISSION,
                    action = SubmissionCommands.GetOrganizationsByReferencesFromPacs
                )
            )

        val receivedParties = data.parties
            .map { detail -> detail.toDomain() }
            .let { Parties(it) }

        val updatedParties = context.parties.updateBy(receivedParties)

        context.parties = updatedParties

        return MaybeFail.none()
    }
}
