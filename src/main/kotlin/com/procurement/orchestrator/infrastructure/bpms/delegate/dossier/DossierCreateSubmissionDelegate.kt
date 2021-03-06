package com.procurement.orchestrator.infrastructure.bpms.delegate.dossier

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.DossierClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getDetailsIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.tryGetSubmissions
import com.procurement.orchestrator.application.model.context.members.Outcomes
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.party.Parties
import com.procurement.orchestrator.domain.model.party.Party
import com.procurement.orchestrator.domain.model.party.PartyRole
import com.procurement.orchestrator.domain.model.party.PartyRoles
import com.procurement.orchestrator.domain.model.submission.Details
import com.procurement.orchestrator.domain.model.submission.Submissions
import com.procurement.orchestrator.domain.util.extension.asList
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.CreateSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.QualificationCommands
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class DossierCreateSubmissionDelegate(
    logger: Logger,
    private val client: DossierClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, CreateSubmissionAction.Result>(
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
    ): Result<Reply<CreateSubmissionAction.Result>, Fail.Incident> {

        val submissions = context.tryGetSubmissions()
            .orForwardFail { fail -> return fail }

        val submission = submissions.getDetailsIfOnlyOne()
            .orForwardFail { fail -> return fail }

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo
        return client.createSubmission(
            id = commandId,
            params = CreateSubmissionAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                date = requestInfo.timestamp,
                owner = requestInfo.owner,
                submission = CreateSubmissionAction.Params.Submission(
                    id = submission.id,
                    requirementResponses = submission.requirementResponses,
                    candidates = submission.candidates,
                    documents = submission.documents
                )
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<CreateSubmissionAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.QUALIFICATION,
                    action = QualificationCommands.CreateSubmission
                )
            )

        val submissions = context.tryGetSubmissions()
            .orReturnFail { return MaybeFail.fail(it) }

        val submission = submissions.getDetailsIfOnlyOne()
            .orReturnFail { return MaybeFail.fail(it) }

        val updatedSubmission = submission.copy(
            id = data.id,
            date = data.date,
            status = data.status,
            documents = data.documents,
            requirementResponses = data.requirementResponses
        )

        context.submissions = Submissions(details = Details(updatedSubmission))

        val parties = data.candidates
            .map { reference ->
                Party(
                    id = reference.id,
                    name = reference.name,
                    roles = PartyRoles(PartyRole.CANDIDATE)
                )
            }

        context.parties = Parties(parties)

        createOutcomes(context = context, data = data)

        return MaybeFail.none()
    }

    private fun createOutcomes(
        context: CamundaGlobalContext,
        data: CreateSubmissionAction.Result
    ) {
        val platformId = context.requestInfo.platformId
        val outcomes = context.outcomes ?: Outcomes()
        val details = outcomes[platformId] ?: Outcomes.Details()

        val newOutcomes = data
            .let { submission -> Outcomes.Details.Submission(id = submission.id, token = submission.token) }
            .asList()

        val updatedDetails = details.copy(submissions = newOutcomes)
        outcomes[platformId] = updatedDetails
        context.outcomes = outcomes
    }
}
