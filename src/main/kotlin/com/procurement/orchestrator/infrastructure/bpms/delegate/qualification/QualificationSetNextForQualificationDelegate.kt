package com.procurement.orchestrator.infrastructure.bpms.delegate.qualification

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.QualificationClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getDetailsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.tryGetSubmissions
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.organization.OrganizationReference
import com.procurement.orchestrator.domain.model.person.Person
import com.procurement.orchestrator.domain.model.qualification.Qualification
import com.procurement.orchestrator.domain.model.qualification.Qualifications
import com.procurement.orchestrator.domain.model.requirement.RequirementReference
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponse
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponses
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.SetNextForQualificationAction
import org.springframework.stereotype.Component

@Component
class QualificationSetNextForQualificationDelegate(
    logger: Logger,
    private val qualificationClient: QualificationClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, SetNextForQualificationAction.Result>(
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
    ): Result<Reply<SetNextForQualificationAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        val submissions = context.tryGetSubmissions()
            .orForwardFail { fail -> return fail }
            .getDetailsIfNotEmpty()
            .orForwardFail { fail -> return fail }

        val tender = context.tryGetTender()
            .orForwardFail { fail -> return fail }

        return qualificationClient.setNextForQualificationAction(
            id = commandId,
            params = SetNextForQualificationAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                submissions = submissions.map { submission ->
                    SetNextForQualificationAction.Params.Submission(
                        id = submission.id,
                        date = submission.date
                    )
                },
                tender = SetNextForQualificationAction.Params.Tender(
                    otherCriteria = tender.otherCriteria
                        ?.let { otherCriteria ->
                            SetNextForQualificationAction.Params.Tender.OtherCriteria(
                                qualificationSystemMethods = otherCriteria.qualificationSystemMethods,
                                reductionCriteria = otherCriteria.reductionCriteria
                            )
                        }
                )
                ,
                criteria = tender.criteria.map { criterion ->
                    SetNextForQualificationAction.Params.Criteria(
                        id = criterion.id
                    )
                }

            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<SetNextForQualificationAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.none()

        val requestQualifications = data.qualifications
            .map { qualification ->
                Qualification(
                    id = qualification.id,
                    internalId = qualification.internalId,
                    date = qualification.date,
                    status = qualification.status,
                    statusDetails = qualification.statusDetails,
                    relatedSubmission = qualification.relatedSubmission,
                    scoring = qualification.scoring,
                    documents = Documents(
                        qualification.documents
                            ?.map { document ->
                                Document(
                                    id = document.id,
                                    documentType = document.documentType,
                                    title = document.title,
                                    description = document.description
                                )
                            }
                            .orEmpty()
                    ),
                    requirementResponses = RequirementResponses(
                        qualification.requirementResponses
                            ?.map { requirementResponse ->
                                RequirementResponse(
                                    id = requirementResponse.id,
                                    value = requirementResponse.value,
                                    requirement = RequirementReference(
                                        id = requirementResponse.requirement.id
                                    ),
                                    relatedTenderer = OrganizationReference(
                                        id = requirementResponse.relatedTenderer.id
                                    ),
                                    responder = requirementResponse.responder
                                        .let { responder ->
                                            Person(
                                                id = responder.id,
                                                name = responder.name
                                            )
                                        }
                                )

                            }.orEmpty()
                    )
                )
            }

        val updatedQualifications = context.qualifications updateBy Qualifications(requestQualifications)

        context.qualifications = updatedQualifications

        return MaybeFail.none()
    }
}
