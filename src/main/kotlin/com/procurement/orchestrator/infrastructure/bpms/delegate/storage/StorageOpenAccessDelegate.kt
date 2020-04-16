package com.procurement.orchestrator.infrastructure.bpms.delegate.storage

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.StorageClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getAmendmentIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getAwardIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getBusinessFunctionsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getRequirementResponseIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getResponder
import com.procurement.orchestrator.application.model.context.members.Awards
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.functional.bind
import com.procurement.orchestrator.domain.model.amendment.Amendment
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.storage.action.OpenAccessAction
import org.springframework.stereotype.Component

@Component
class StorageOpenAccessDelegate(
    logger: Logger,
    private val client: StorageClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<StorageOpenAccessDelegate.Parameters, OpenAccessAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val entities: List<Entity> = parameterContainer.getListString("entities")
            .orForwardFail { fail -> return fail }
            .map {
                Entity.orNull(it)
                    ?: return failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = "entities",
                            expectedValues = OperationTypeProcess.allowedElements.keysAsStrings(),
                            actualValue = it
                        )
                    )
            }

        return success(Parameters(entities = entities))
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<OpenAccessAction.Result>, Fail.Incident> {

        val tender = context.tender
        val entities = parameters.entities.toSet()

        val documentOfAmendmentsOfTender: List<DocumentId> = if (Entity.AMENDMENT in entities) {
            if (tender == null)
                return failure(Fail.Incident.Bpms.Context.Missing(name = "tender"))
            tender.getAmendmentIfOnlyOne()
                .orForwardFail { fail -> return fail }
                .documents
                .map { document -> document.id }
        } else
            emptyList()

        val documentOfTender: List<DocumentId> = if (Entity.TENDER in entities) {
            if (tender == null)
                return failure(Fail.Incident.Bpms.Context.Missing(name = "tender"))
            tender.documents
                .map { document -> document.id }
        } else
            emptyList()

        val documentOfAwards: List<DocumentId> = if (Entity.AWARD_REQUIREMENT_RESPONSE in entities)
            context.getAwardIfOnlyOne()
                .orForwardFail { fail -> return fail }
                .getRequirementResponseIfOnlyOne()
                .orForwardFail { fail -> return fail }
                .getResponder()
                .orForwardFail { fail -> return fail }
                .getBusinessFunctionsIfNotEmpty(path = "awards.requirementResponses.responder")
                .orForwardFail { fail -> return fail }
                .flatMap { businessFunction ->
                    businessFunction.documents.map { document -> document.id }
                }
        else
            emptyList()

        val documentIds = documentOfAmendmentsOfTender + documentOfTender + documentOfAwards

        if (documentIds.isEmpty())
            return success(Reply.None)

        val requestInfo = context.requestInfo
        return client.openAccess(
            id = commandId,
            params = OpenAccessAction.Params(
                datePublished = requestInfo.timestamp,
                ids = documentIds
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        data: OpenAccessAction.Result
    ): MaybeFail<Fail.Incident> {

        if (data.isEmpty())
            return MaybeFail.none()

        val documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document> = data.associateBy { it.id }
        val entities = parameters.entities.toSet()
        val tender = context.tender

        val updatedAmendments = if (Entity.AMENDMENT in entities)
            tender
                ?.updateAmendmentDocuments(documentsByIds)
                ?.orReturnFail { return MaybeFail.fail(it) }
                .orEmpty()
        else
            tender?.amendments.orEmpty()

        val updatedTenderDocuments = if (Entity.TENDER in entities)
            tender
                ?.updateTenderDocuments(documentsByIds)
                ?.orReturnFail { return MaybeFail.fail(it) }
                .orEmpty()
        else
            tender?.documents.orEmpty()

        if (tender != null)
            context.tender = tender.copy(
                documents = updatedTenderDocuments,
                amendments = updatedAmendments
            )

        val updatedAwards: Awards = success(context.awards)
            .bind {
                if (Entity.AWARD_REQUIREMENT_RESPONSE in entities)
                    it.updateDocuments(documentsByIds)
                else
                    success(it)
            }
            .orReturnFail { return MaybeFail.fail(it) }

        context.awards = updatedAwards

        return MaybeFail.none()
    }

    private fun Tender.updateAmendmentDocuments(documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>): Result<List<Amendment>, Fail.Incident.Bpms> =
        this.amendments
            .map { amendment ->
                val updatedDocuments = amendment.documents
                    .map { document ->
                        documentsByIds[document.id]
                            ?.let {
                                document.copy(
                                    datePublished = it.datePublished,
                                    url = it.uri
                                )
                            }
                            ?: return failure(
                                Fail.Incident.Bpms.Context.UnConsistency.Update(
                                    name = "document",
                                    path = "tender.amendments[id:${amendment.id}].documents[id:${document.id}]",
                                    id = document.id.toString()
                                )
                            )
                    }
                amendment.copy(documents = updatedDocuments)
            }
            .asSuccess<List<Amendment>, Fail.Incident.Bpms>()

    private fun Tender.updateTenderDocuments(documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>): Result<List<Document>, Fail.Incident.Bpms> =
        this.documents
            .map { document ->
                documentsByIds[document.id]
                    ?.let {
                        document.copy(
                            datePublished = it.datePublished,
                            url = it.uri
                        )
                    }
                    ?: return failure(
                        Fail.Incident.Bpms.Context.UnConsistency.Update(
                            name = "document",
                            path = "tender.documents[id:${document.id}]",
                            id = document.id.toString()
                        )
                    )
            }
            .asSuccess<List<Document>, Fail.Incident.Bpms>()

    private fun Awards.updateDocuments(documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>): Result<Awards, Fail.Incident.Bpms> {
        val updatedAwards = this.map { award ->
            val updatedRequirementResponses = award.requirementResponses
                .map { requirementResponse ->
                    val responder = requirementResponse.responder
                    if (responder != null) {
                        val updatedResponder = responder.let { person ->
                            val updatedBusinessFunctions = person.businessFunctions
                                .map { businessFunction ->
                                    val updatedDocuments = businessFunction.documents
                                        .map { document ->
                                            documentsByIds[document.id]
                                                ?.let {
                                                    document.copy(datePublished = it.datePublished, url = it.uri)
                                                }
                                                ?: return failure(
                                                    Fail.Incident.Bpms.Context.UnConsistency.Update(
                                                        name = "document",
                                                        path = "awards[id:$award.id].requirementResponse[id:${requirementResponse.id}].responder.businessFunctions[id:${businessFunction.id}]",
                                                        id = document.id.toString()
                                                    )
                                                )
                                        }
                                    businessFunction.copy(documents = updatedDocuments)
                                }
                            person.copy(businessFunctions = updatedBusinessFunctions)
                        }
                        requirementResponse.copy(responder = updatedResponder)
                    } else
                        requirementResponse
                }
            award.copy(requirementResponses = updatedRequirementResponses)
        }
        return success(Awards(updatedAwards))
    }

    class Parameters(
        val entities: List<Entity>
    )

    enum class Entity(override val key: String) : EnumElementProvider.Key {

        AMENDMENT("amendment"),
        AWARD_REQUIREMENT_RESPONSE("award.requirementResponse"),
        TENDER("tender");

        override fun toString(): String = key

        companion object : EnumElementProvider<Entity>(info = info())
    }
}
