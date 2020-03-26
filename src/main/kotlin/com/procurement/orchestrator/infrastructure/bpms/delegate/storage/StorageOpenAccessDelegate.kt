package com.procurement.orchestrator.infrastructure.bpms.delegate.storage

import com.procurement.orchestrator.application.client.StorageClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
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
            .doOnError { return failure(it) }
            .get
            .map {
                Entity.orNull(it)
                    ?: return failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = "entities",
                            expectedValues = OperationTypeProcess.allowedValues,
                            actualValue = it
                        )
                    )
            }

        return success(Parameters(entities = entities))
    }

    override suspend fun execute(
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<OpenAccessAction.Result>, Fail.Incident> {

        val tender = context.tender
            ?: Fail.Incident.Bpe(description = "The global context does not contain a 'Tender' object.")
                .throwIncident()

        val documentIds: List<DocumentId> = parameters.entities
            .asSequence()
            .flatMap { entity ->
                when (entity) {
                    Entity.AMENDMENT -> tender.amendments
                        .asSequence()
                        .flatMap { amendment -> amendment.documents.asSequence() }
                        .map { document -> document.id }

                    Entity.TENDER -> tender.documents
                        .asSequence()
                        .map { document -> document.id }
                }
            }
            .toList()

        return client.openAccess(params = OpenAccessAction.Params(documentIds))
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        data: OpenAccessAction.Result
    ): MaybeFail<Fail.Incident.Bpmn> {
        val tender = context.tender
            ?: Fail.Incident.Bpe(description = "The global context does not contain a 'Tender' object.")
                .throwIncident()

        val documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document> = data.associateBy { it.id }

        val updatedTender: Tender =
            updateDocuments(entities = parameters.entities.toSet(), tender = tender, documentsByIds = documentsByIds)
                .doOnError { return MaybeFail.fail(it) }
                .get

        context.tender = updatedTender

        return MaybeFail.none()
    }

    private tailrec fun updateDocuments(
        entities: Set<Entity>,
        tender: Tender,
        documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>
    ): Result<Tender, Fail.Incident.Bpmn> {
        if (entities.isEmpty())
            return success(tender)

        val entity = entities.first()
        val updatedTender = when (entity) {
            Entity.AMENDMENT -> tender.updateAmendmentDocuments(documentsByIds)
            Entity.TENDER -> tender.updateTenderDocuments(documentsByIds)
        }
            .doOnError { return failure(it) }
            .get

        return updateDocuments(entities = entities - entity, tender = updatedTender, documentsByIds = documentsByIds)
    }

    private fun Tender.updateAmendmentDocuments(documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>): Result<Tender, Fail.Incident.Bpmn> {
        val updatedAmendments = this.amendments
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
                                Fail.Incident.Bpmn.Context.UnConsistency(
                                    name = "tender.amendments[id:${amendment.id}].documents[id:${document.id}]",
                                    description = "Document '${document.id}' for update is not found."
                                )
                            )
                    }
                amendment.copy(documents = updatedDocuments)
            }

        return success(this.copy(amendments = updatedAmendments))
    }

    private fun Tender.updateTenderDocuments(documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>): Result<Tender, Fail.Incident.Bpmn> {
        val updatedDocuments = this.documents
            .map { document ->
                documentsByIds[document.id]
                    ?.let {
                        document.copy(
                            datePublished = it.datePublished,
                            url = it.uri
                        )
                    }
                    ?: return failure(
                        Fail.Incident.Bpmn.Context.UnConsistency(
                            name = "tender.documents[id:${document.id}]",
                            description = "Document '${document.id}' for update is not found."
                        )
                    )
            }
        return success(this.copy(documents = updatedDocuments))
    }

    class Parameters(
        val entities: List<Entity>
    )

    enum class Entity(override val key: String) : EnumElementProvider.Key {

        AMENDMENT("amendment"),
        TENDER("tender");

        override fun toString(): String = key

        companion object : EnumElementProvider<Entity>(info = info())
    }
}
