package com.procurement.orchestrator.infrastructure.bpms.delegate.dossier

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.DossierClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getCandidatesIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getDetailsIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.tryGetSubmissions
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.candidate.Candidates
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.ValidateSubmissionAction
import org.springframework.stereotype.Component

@Component
class DossierValidateSubmissionDelegate(
    logger: Logger,
    private val client: DossierClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, Unit>(
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
    ): Result<Reply<Unit>, Fail.Incident> {

        val submissions = context.tryGetSubmissions()
            .orForwardFail { fail -> return fail }

        val submission = submissions.getDetailsIfOnlyOne()
            .orForwardFail { fail -> return fail }

        val submissionCandidates = submission.getCandidatesIfNotEmpty()
            .orForwardFail { fail -> return fail }

        val processInfo = context.processInfo

        return client.validateSubmission(
            id = commandId,
            params = ValidateSubmissionAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                id = submission.id,
                candidates = Candidates(submissionCandidates),
                documents = submission.documents,
                requirementResponses = submission.requirementResponses
                    .map { requirementResponse ->
                        ValidateSubmissionAction.Params.RequirementResponse(
                            id = requirementResponse.id,
                            value = requirementResponse.value,
                            requirement = requirementResponse.requirement
                                ?.let { requirement ->
                                    ValidateSubmissionAction.Params.RequirementResponse.Requirement(
                                        requirement.id
                                    )
                                },
                            relatedCandidate = requirementResponse.relatedCandidate
                                ?.let { tenderer ->
                                    ValidateSubmissionAction.Params.RequirementResponse.RelatedCandidate(
                                        name = tenderer.name,
                                        id = tenderer.id
                                    )
                                },
                            evidences = requirementResponse.evidences
                                .map { evidence ->
                                    ValidateSubmissionAction.Params.RequirementResponse.Evidence(
                                        id = evidence.id,
                                        description = evidence.description,
                                        title = evidence.title,
                                        relatedDocument = evidence.relatedDocument?.let { relatedDocument ->
                                            ValidateSubmissionAction.Params.RequirementResponse.Evidence.RelatedDocument(
                                                id = relatedDocument.id
                                            )
                                        }
                                    )
                                }
                        )
                    },
                mdm = context.processMasterData?.mdm
                    ?.let { mdm ->
                        ValidateSubmissionAction.Params.Mdm(
                            registrationSchemes = mdm.registrationSchemes
                                .map { registrationScheme ->
                                    ValidateSubmissionAction.Params.Mdm.RegistrationScheme(
                                        country = registrationScheme.country,
                                        schemes = registrationScheme.schemes.map { it }
                                    )
                                }
                        )
                    }
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<Unit>
    ): MaybeFail<Fail.Incident.Bpmn> = MaybeFail.none()
}
