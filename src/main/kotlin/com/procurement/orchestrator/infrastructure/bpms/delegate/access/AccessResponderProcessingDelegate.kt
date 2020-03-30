package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.award.Award
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.identifier.Identifier
import com.procurement.orchestrator.domain.model.organization.Organization
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunction
import com.procurement.orchestrator.domain.model.organization.person.Person
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.action.ResponderProcessingAction
import org.springframework.stereotype.Component

@Component
class AccessResponderProcessingDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, ResponderProcessingAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<ResponderProcessingAction.Result>, Fail.Incident> {

        val responder = buildResponder(context)
            .doOnError { return failure(it) }
            .get

        val processInfo = context.processInfo
        val cpid: Cpid = processInfo.cpid
        val ocid: Ocid = processInfo.ocid

        val requestInfo = context.requestInfo
        return accessClient.responderProcessing(
            params = ResponderProcessingAction.Params(
                cpid = cpid,
                ocid = ocid,
                startDate = requestInfo.timestamp,
                responder = responder
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        data: ResponderProcessingAction.Result
    ): MaybeFail<Fail.Incident> {
        val party = Organization(
            id = data.id,
            name = data.name,
            identifier = data.identifier
                .let { identifier ->
                    Identifier(
                        scheme = identifier.scheme,
                        id = identifier.id,
                        uri = identifier.uri
                    )
                },
            persons = data.persons
                .map { person ->
                    Person(
                        title = person.title,
                        name = person.name,
                        identifier = person.identifier
                            .let { identifier ->
                                Identifier(
                                    scheme = identifier.scheme,
                                    id = identifier.id,
                                    uri = identifier.uri
                                )
                            },
                        businessFunctions = person.businessFunctions
                            .map { businessFunction ->
                                BusinessFunction(
                                    id = businessFunction.id,
                                    type = businessFunction.type,
                                    jobTitle = businessFunction.jobTitle,
                                    period = Period(startDate = businessFunction.period.startDate),
                                    documents = businessFunction.documents
                                        .map { document ->
                                            Document(
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
        )
        context.parties = listOf(party)
        return MaybeFail.none()
    }

    private fun buildResponder(context: CamundaGlobalContext): Result<ResponderProcessingAction.Params.Responder, Fail.Incident> {
        val awards: List<Award> = context.awards
            .takeIf { it.isNotEmpty() }
            ?: return failure(Fail.Incident.Bpe(description = "The global context does not contain a 'Awards' object."))

        if (awards.size != 1) return failure(
            Fail.Incident.Bpmn.Context.UnConsistency(
                name = "awards",
                description = "It was expected that the attribute 'awards' would have only one value. In fact, the attribute has ${awards.size} meanings"
            )
        )

        val award = awards[0]
        if (award.requirementResponses.size != 1) return failure(
            Fail.Incident.Bpmn.Context.UnConsistency(
                name = "award.requirementResponses",
                description = "It was expected that the attribute 'award.requirementResponses' would have only one value. In fact, the attribute has ${award.requirementResponses.size} meanings"
            )
        )
        val responder = award.requirementResponses[0]
            .responder
            ?.let { responder ->
                ResponderProcessingAction.Params.Responder(
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
            ?: return failure(
                Fail.Incident.Bpmn.Context.UnConsistency(
                    name = "award.requirementResponses.responder",
                    description = "It was expected that the attribute 'award.requirementResponses.responder' would have only one value. In fact, the attribute without."
                )
            )

        return success(responder)
    }
}
