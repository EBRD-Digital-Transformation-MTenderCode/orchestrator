package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getAwardIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getRequirementResponseIfOnlyOne
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponse
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.action.VerifyRequirementResponseAction
import org.springframework.stereotype.Component

@Component
class VerifyRequirementResponseDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
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

        val processInfo = context.processInfo
        val cpid: Cpid = processInfo.cpid
        val ocid: Ocid = processInfo.ocid

        val award = context.getAwardIfOnlyOne()
            .orForwardFail { error -> return error }

        val requirementResponse = award.getRequirementResponseIfOnlyOne()
            .orForwardFail { error -> return error }

        val responder = convertResponder(context, requirementResponse)
            .orForwardFail { error -> return error }

        return accessClient.verifyRequirementResponse(
            id = commandId,
            params = VerifyRequirementResponseAction.Params(
                cpid = cpid,
                ocid = ocid,
                requirementResponseId = requirementResponse.id,
                value = requirementResponse.value!!,
                responder = responder,
                requirementId = requirementResponse.requirement!!.id
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        data: Unit
    ): MaybeFail<Fail.Incident.Bpmn> = MaybeFail.none()

    private fun convertResponder(
        context: CamundaGlobalContext,
        requirementResponse: RequirementResponse
    ): Result<VerifyRequirementResponseAction.Params.Person, Fail.Incident> {

        val responder  = requirementResponse.responder!!
            .let { responder ->
                VerifyRequirementResponseAction.Params.Person(
                    title = responder.title,
                    name = responder.name,
                    identifier = responder.identifier
                        .let { identifier ->
                            VerifyRequirementResponseAction.Params.Person.Identifier(
                                scheme = identifier.scheme,
                                id = identifier.id,
                                uri = identifier.uri
                            )
                        },
                    businessFunctions = responder.businessFunctions
                        .map { businessFunction ->
                            VerifyRequirementResponseAction.Params.Person.BusinessFunction(
                                id = businessFunction.id,
                                type = businessFunction.type,
                                jobTitle = businessFunction.jobTitle,
                                period = businessFunction.period
                                    ?.let { period ->
                                        VerifyRequirementResponseAction.Params.Person.BusinessFunction.Period(
                                            startDate = period.startDate
                                        )
                                    },
                                documents = businessFunction.documents
                                    .map { document ->
                                        VerifyRequirementResponseAction.Params.Person.BusinessFunction.Document(
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

        return success(responder)
    }
}
