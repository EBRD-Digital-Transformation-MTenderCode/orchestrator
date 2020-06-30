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
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.organization.OrganizationReference
import com.procurement.orchestrator.domain.model.person.Person
import com.procurement.orchestrator.domain.model.qualification.Qualifications
import com.procurement.orchestrator.domain.model.requirement.RequirementReference
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponse
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponses
import com.procurement.orchestrator.domain.util.extension.asList
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.qualification.QualificationCommands
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.FindRequirementResponseByIdsAction
import org.springframework.stereotype.Component

@Component
class QualificationFindRequirementResponseByIdsDelegate(
    logger: Logger,
    private val qualificationClient: QualificationClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, FindRequirementResponseByIdsAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {
    companion object{
        private const val QUALIFICATION_ID_ATTRIBUTE_NAME = "qualification.id"
    }
    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<FindRequirementResponseByIdsAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val qualification = context.getQualificationIfOnlyOne()
            .orForwardFail { fail -> return fail }

        return qualificationClient.findRequirementResponseByIds(
            id = commandId,
            params = FindRequirementResponseByIdsAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                qualificationId = qualification.id,
                requirementResponseIds = qualification.requirementResponses.map { it.id }
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<FindRequirementResponseByIdsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.none()

        val contextQualification = context.getQualificationIfOnlyOne()
            .orReturnFail { return MaybeFail.fail(it) }

        if (contextQualification.id != data.qualification.id)
            MaybeFail.fail(
                Fail.Incident.Response.Validation.UnknownEntity(
                    id = data.qualification.id.toString(), name = QUALIFICATION_ID_ATTRIBUTE_NAME
                )
            )

        val contextRequirementResponses = contextQualification.requirementResponses
        val requestRequirementResponses = RequirementResponses(
            data.qualification.requirementResponses
                .map { requirementResponse ->
                    RequirementResponse(
                        id = requirementResponse.id,
                        value = requirementResponse.value,
                        relatedTenderer = OrganizationReference(id = requirementResponse.relatedTenderer.id),
                        requirement = RequirementReference(id = requirementResponse.requirement.id),
                        responder = requirementResponse.responder.let { responder ->
                            Person(
                                id = responder.id,
                                name = responder.name
                            )
                        }
                    )
                }
        )

        val updatedRequirementResponses = contextRequirementResponses updateBy requestRequirementResponses
        val updatedQualifications = contextQualification.copy(requirementResponses = updatedRequirementResponses)
            .asList()

        context.qualifications = Qualifications(updatedQualifications)

        return MaybeFail.none()
    }
}
