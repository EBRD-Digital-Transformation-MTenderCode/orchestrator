package com.procurement.orchestrator.infrastructure.bpms.delegate.qualification

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.QualificationClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getQualificationIfOnlyOne
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
import com.procurement.orchestrator.infrastructure.client.web.qualification.QualificationCommands
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.DoQualificationAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class QualificationDoQualificationDelegate(
    logger: Logger,
    private val qualificationClient: QualificationClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, DoQualificationAction.Result>(
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
    ): Result<Reply<DoQualificationAction.Result>, Fail.Incident> {



        val qualification = context.getQualificationIfOnlyOne()
            .orForwardFail { fail -> return fail }

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo
        return qualificationClient.doQualification(
            id = commandId,
            params = DoQualificationAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                date = requestInfo.timestamp,
                qualifications = listOf(
                    DoQualificationAction.Params.Qualification(
                        id = qualification.id,
                        internalId = qualification.internalId,
                        statusDetails = qualification.statusDetails,
                        description = qualification.description,
                        documents = qualification.documents.map { document ->
                            DoQualificationAction.Params.Qualification.Document(
                                id = document.id,
                                description = document.description,
                                title = document.title,
                                documentType = document.documentType
                            )
                        }
                    )
                )
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<DoQualificationAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.QUALIFICATION,
                    action = QualificationCommands.CreateQualification
                )
            )

        val qualifications = data.qualifications
            ?.map { qualification ->
                Qualification(
                    id = qualification.id,
                    internalId = qualification.internalId,
                    status = qualification.status,
                    statusDetails = qualification.statusDetails,
                    date = qualification.date,
                    documents = Documents(
                        qualification.documents
                            ?.map { document ->
                                Document(
                                    id = document.id,
                                    description = document.description,
                                    documentType = document.documentType,
                                    title = document.title
                                )
                            }.orEmpty()
                    ),
                    relatedSubmission = qualification.relatedSubmission,
                    requirementResponses = RequirementResponses(
                        qualification.requirementResponses
                            ?.map { requirementResponse ->
                                RequirementResponse(
                                    id = requirementResponse.id,
                                    value = requirementResponse.value,
                                    relatedTenderer = OrganizationReference(id = requirementResponse.relatedTenderer.id),
                                    requirement = RequirementReference(id = requirementResponse.requirement.id),
                                    responder = requirementResponse.responder
                                        .let { responder ->
                                            Person(
                                                id = responder.id,
                                                name = responder.name
                                            )
                                        }
                                )
                            }.orEmpty()
                    ),
                    scoring = qualification.scoring,
                    description = qualification.description
                )
            }.orEmpty()

        context.qualifications = Qualifications(qualifications)

        return MaybeFail.none()
    }
}
