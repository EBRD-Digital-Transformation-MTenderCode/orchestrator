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
import com.procurement.orchestrator.domain.model.candidate.Candidates
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.organization.Organization
import com.procurement.orchestrator.domain.model.organization.OrganizationReference
import com.procurement.orchestrator.domain.model.requirement.RequirementReference
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponse
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponses
import com.procurement.orchestrator.domain.model.submission.Details
import com.procurement.orchestrator.domain.model.submission.Submission
import com.procurement.orchestrator.domain.model.submission.Submissions
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.FindSubmissionsAction
import org.springframework.stereotype.Component

@Component
class DossierFindSubmissionsForOpeningDelegate(
    logger: Logger,
    private val dossierClient: DossierClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, FindSubmissionsAction.Result>(
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
    ): Result<Reply<FindSubmissionsAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo
        return dossierClient.findSubmissions(
            id = commandId,
            params = FindSubmissionsAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                pmd = processInfo.pmd,
                country = requestInfo.country,
                operationType = processInfo.operationType
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<FindSubmissionsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.none()

        val buildSubmissions = buildSubmissions(data)

        val submissions = context.submissions
        context.submissions = if (submissions == null)
            Submissions(Details(buildSubmissions))
        else {
            val receivedSubmissionsById = buildSubmissions.associateBy { it.id }
            val knownSubmissions = submissions.details.associateBy { it.id }
            val updatedSubmissions = submissions.details.map { submission ->
                receivedSubmissionsById[submission.id]
                    ?.let { receivedSubmission ->
                        submission updateBy receivedSubmission
                    }
                    ?: submission
            }
            val newSubmissions = buildSubmissions
                .filter { submission ->
                    submission.id !in knownSubmissions
                }
            Submissions(Details(updatedSubmissions + newSubmissions))
        }

        return MaybeFail.none()
    }

    private fun buildSubmissions(submissions: List<FindSubmissionsAction.Result.Submission>) =
        submissions.map { submission ->
            Submission(
                id = submission.id,
                status = submission.status,
                date = submission.date,
                candidates = Candidates(
                    submission.candidates
                        .map { candidate ->
                            Organization(
                                id = candidate.id,
                                name = candidate.name
                            )
                        }
                ),
                documents = Documents(
                    submission.documents
                        ?.map { document ->
                            Document(
                                id = document.id,
                                description = document.description,
                                title = document.title,
                                documentType = document.documentType
                            )
                        }.orEmpty()
                ),
                requirementResponses = RequirementResponses(
                    submission.requirementResponses
                        ?.map { requirementResponse ->
                            RequirementResponse(
                                id = requirementResponse.id,
                                value = requirementResponse.value,
                                requirement = requirementResponse.requirement
                                    .let { requirement ->
                                        RequirementReference(
                                            id = requirement.id
                                        )
                                    },
                                relatedCandidate = requirementResponse.relatedCandidate
                                    .let { relatedCandidate ->
                                        OrganizationReference(
                                            id = relatedCandidate.id,
                                            name = relatedCandidate.name
                                        )
                                    }
                            )
                        }.orEmpty()
                )
            )
        }
}
