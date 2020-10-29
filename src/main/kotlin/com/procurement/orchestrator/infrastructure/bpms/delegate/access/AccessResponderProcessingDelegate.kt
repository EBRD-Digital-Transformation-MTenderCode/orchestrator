package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getAwardIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getQualificationIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getRequirementResponseIfOnlyOne
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.award.Awards
import com.procurement.orchestrator.domain.model.person.Person
import com.procurement.orchestrator.domain.model.qualification.Qualifications
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponse
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponses
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.AccessCommands
import com.procurement.orchestrator.infrastructure.client.web.access.action.ResponderProcessingAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class AccessResponderProcessingDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<AccessResponderProcessingDelegate.Parameters, ResponderProcessingAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        private const val PARAMETER_NAME_LOCATION = "location"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val location: Location = parameterContainer.getString(PARAMETER_NAME_LOCATION)
            .orForwardFail { fail -> return fail }
            .let {
                Location.orNull(it)
                    ?: return failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = PARAMETER_NAME_LOCATION,
                            expectedValues = Location.allowedElements.keysAsStrings(),
                            actualValue = it
                        )
                    )
            }
        return Parameters(location = location)
            .asSuccess()
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<ResponderProcessingAction.Result>, Fail.Incident> {

        val responder = buildResponder(context, parameters.location)
            .orForwardFail { fail -> return fail }

        val processInfo = context.processInfo
        val cpid: Cpid = processInfo.cpid!!
        val ocid: Ocid = processInfo.ocid!!

        val requestInfo = context.requestInfo
        return accessClient.responderProcessing(
            id = commandId,
            params = ResponderProcessingAction.Params(
                cpid = cpid,
                ocid = ocid,
                date = requestInfo.timestamp,
                responder = responder
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        result: Option<ResponderProcessingAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(service = ExternalServiceName.ACCESS, action = AccessCommands.ResponderProcessing)
            )

        when (parameters.location) {
            Location.AWARD -> {
                val award = context.getAwardIfOnlyOne()
                    .orReturnFail { return MaybeFail.fail(it) }

                val requirementResponse = award.getRequirementResponseIfOnlyOne()
                    .orReturnFail { return MaybeFail.fail(it) }

                val updatedRequirementResponse = requirementResponse.copy(
                    responder = Person(
                        id = data.id,
                        name = data.name
                    )
                )
                val updatedAward = award.copy(
                    requirementResponses = RequirementResponses(updatedRequirementResponse)
                )
                context.awards = Awards(updatedAward)
            }
            Location.QUALIFICATION -> {
                val qualification = context.getQualificationIfOnlyOne()
                    .orReturnFail { return MaybeFail.fail(it) }

                val requirementResponse = qualification.getRequirementResponseIfOnlyOne()
                    .orReturnFail { return MaybeFail.fail(it) }

                val updatedRequirementResponse = requirementResponse.copy(
                    responder = Person(
                        id = data.id,
                        name = data.name
                    )
                )
                val updatedQualification = qualification.copy(
                    requirementResponses = RequirementResponses(updatedRequirementResponse)
                )
                context.qualifications = Qualifications(updatedQualification)
            }
        }

        return MaybeFail.none()
    }

    private fun buildResponder(
        context: CamundaGlobalContext,
        location: Location
    ): Result<ResponderProcessingAction.Params.Responder, Fail.Incident> {

        val responder = when (location) {
            Location.AWARD -> {
                val award = context.getAwardIfOnlyOne()
                    .orForwardFail { fail -> return fail }
                val requirementResponse = award.getRequirementResponseIfOnlyOne()
                    .orForwardFail { fail -> return fail }

                convertRequirementResponseToResponder(requirementResponse)
                    ?: return failure(
                        Fail.Incident.Bpms.Context.Missing(name = "responder", path = "award.requirementResponses")
                    )
            }
            Location.QUALIFICATION -> {
                val qualification = context.getQualificationIfOnlyOne()
                    .orForwardFail { fail -> return fail }
                val requirementResponse = qualification.getRequirementResponseIfOnlyOne()
                    .orForwardFail { fail -> return fail }

                convertRequirementResponseToResponder(requirementResponse)
                    ?: return failure(
                        Fail.Incident.Bpms.Context.Missing(
                            name = "responder",
                            path = "qualification.requirementResponses"
                        )
                    )
            }
        }

        return success(responder)
    }

    private fun convertRequirementResponseToResponder(requirementResponse: RequirementResponse): ResponderProcessingAction.Params.Responder? =
        requirementResponse.responder
            ?.let { responder ->
                ResponderProcessingAction.Params.Responder(
                    id = responder.id,
                    title = responder.title,
                    name = responder.name,
                    identifier = responder.identifier
                        ?.let { identifier ->
                            ResponderProcessingAction.Params.Responder.Identifier(
                                scheme = identifier.scheme,
                                id = identifier.id,
                                uri = identifier.uri
                            )
                        },
                    businessFunctions = responder.businessFunctions
                        .map { businessFunction ->
                            ResponderProcessingAction.Params.Responder.BusinessFunction(
                                id = businessFunction.id,
                                type = businessFunction.type,
                                jobTitle = businessFunction.jobTitle,
                                period = businessFunction.period
                                    ?.let { period ->
                                        ResponderProcessingAction.Params.Responder.BusinessFunction.Period(
                                            startDate = period.startDate
                                        )
                                    },
                                documents = businessFunction.documents
                                    .map { document ->
                                        ResponderProcessingAction.Params.Responder.BusinessFunction.Document(
                                            documentType = document.documentType,
                                            id = document.id,
                                            title = document.title,
                                            description = document.description
                                        )
                                    }
                            )
                        }
                )
            }

    class Parameters(val location: Location)

    enum class Location(override val key: String) : EnumElementProvider.Key {
        AWARD("award"),
        QUALIFICATION("qualification");

        override fun toString(): String = key

        companion object : EnumElementProvider<Location>(info = info()) {

            @JvmStatic
            @JsonCreator
            fun creator(name: String) = orThrow(name)
        }
    }
}
