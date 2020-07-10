package com.procurement.orchestrator.infrastructure.bpms.delegate.qualification

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.QualificationClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getQualificationIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getRelatedTenderer
import com.procurement.orchestrator.application.model.context.extension.getRequirement
import com.procurement.orchestrator.application.model.context.extension.getRequirementResponseIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getResponder
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
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
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.DoDeclarationAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class QualificationDoDeclarationDelegate(
    logger: Logger,
    private val qualificationClient: QualificationClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, DoDeclarationAction.Result>(
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
    ): Result<Reply<DoDeclarationAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        val qualification = context.getQualificationIfOnlyOne()
            .orForwardFail { fail -> return fail }

        val requirementResponse = qualification.getRequirementResponseIfOnlyOne()
            .orForwardFail { fail -> return fail }

        val responder = requirementResponse.getResponder()
            .orForwardFail { fail -> return fail }

        val relatedTenderer = requirementResponse.getRelatedTenderer()
            .orForwardFail { fail -> return fail }

        val requirement = requirementResponse.getRequirement()
            .orForwardFail { fail -> return fail }

        return qualificationClient.doDeclaration(
            id = commandId,
            params = DoDeclarationAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                qualifications = listOf(
                    DoDeclarationAction.Params.Qualification(
                        id = qualification.id,
                        requirementResponses = listOf(
                            DoDeclarationAction.Params.Qualification.RequirementResponse(
                                id = requirementResponse.id,
                                value = requirementResponse.value,
                                responder = DoDeclarationAction.Params.Qualification.RequirementResponse.Responder(
                                    id = responder.id,
                                    name = responder.name
                                ),
                                relatedTenderer = DoDeclarationAction.Params.Qualification.RequirementResponse.RelatedTenderer(
                                    id = relatedTenderer.id
                                ),
                                requirement = DoDeclarationAction.Params.Qualification.RequirementResponse.Requirement(
                                    id = requirement.id
                                )
                            )
                        )
                    )
                )
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<DoDeclarationAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.QUALIFICATION,
                    action = QualificationCommands.DoDeclaration
                )
            )

        val qualifications = data.qualifications
            .map { qualification ->
                Qualification(
                    id = qualification.id,
                    requirementResponses = RequirementResponses(
                        qualification.requirementResponses
                            .map { requirementResponse ->
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

                            }
                    )
                )
            }

        context.qualifications = Qualifications(qualifications)

        return MaybeFail.none()
    }
}
