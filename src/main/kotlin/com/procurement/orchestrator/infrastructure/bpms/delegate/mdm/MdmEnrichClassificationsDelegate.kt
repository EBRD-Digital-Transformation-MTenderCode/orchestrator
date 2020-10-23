package com.procurement.orchestrator.infrastructure.bpms.delegate.mdm

import com.procurement.orchestrator.application.client.MdmClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getItemsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getLotsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.model.context.members.Errors
import com.procurement.orchestrator.application.model.context.members.Incident
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.extension.date.format
import com.procurement.orchestrator.domain.extension.date.nowDefaultUTC
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.classification.Classification
import com.procurement.orchestrator.domain.model.item.Item
import com.procurement.orchestrator.domain.model.item.Items
import com.procurement.orchestrator.domain.model.lot.Lot
import com.procurement.orchestrator.domain.model.lot.Lots
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractBatchRestDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichClassificationsAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetClassification
import com.procurement.orchestrator.infrastructure.configuration.property.GlobalProperties
import org.springframework.stereotype.Component

@Component
class MdmEnrichClassificationsDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    transform: Transform,
    private val mdmClient: MdmClient
) : AbstractBatchRestDelegate<MdmEnrichClassificationsDelegate.Parameters, EnrichClassificationsAction.Params, List<Classification>>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val locations: Map<EntityKey, EntityValue> = parameterContainer.getMapString("locations")
            .orForwardFail { fail -> return fail }
            .map { (key, value) ->
                val keyParsed = EntityKey.orNull(key)
                    ?: return failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = "locations",
                            expectedValues = EntityKey.allowedElements.keysAsStrings(),
                            actualValue = key
                        )
                    )
                val valueParsed = EntityValue.orNull(value)
                    ?: return failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = "locations",
                            expectedValues = EntityValue.allowedElements.keysAsStrings(),
                            actualValue = value
                        )
                    )
                keyParsed to valueParsed
            }.toMap()

        return success(Parameters(locations = locations))
    }

    override fun prepareSeq(
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<List<EnrichClassificationsAction.Params>, Fail.Incident> {
        val requestInfo = context.requestInfo
        val entities = parameters.locations
        val tender = context.tryGetTender()
            .orForwardFail { fail -> return fail }

        val tenderClassification: Classification? = getTenderClassification(tender, entities)
            .orForwardFail { fail -> return fail }

        val itemsClassifications: List<Classification> = getItemsClassifications(tender, entities)
            .orForwardFail { fail -> return fail }

        val lotsClassifications: List<Classification> = getLotsClassifications(tender, entities)
            .orForwardFail { fail -> return fail }

        val allClassifications: Set<Classification> = mutableSetOf<Classification>()
            .apply {
                tenderClassification?.let { add(it) }
                addAll(itemsClassifications)
                addAll(lotsClassifications)
            }

        val params = allClassifications.map { classification ->
            EnrichClassificationsAction.Params(
                lang = requestInfo.language,
                classificationId = classification.id,
                scheme = classification.scheme
            )
        }

        return success(params)
    }

    override suspend fun execute(
        elements: List<EnrichClassificationsAction.Params>,
        executionInterceptor: ExecutionInterceptor
    ): Result<List<Classification>, Fail.Incident> {

        return elements
            .map { params ->
                val response = mdmClient
                    .enrichClassifications(params = params)
                    .orForwardFail { error -> return error }

                handleResult(response, executionInterceptor)
            }
            .filter { optionalResult -> optionalResult.isDefined }
            .map { result -> result.get }
            .asSuccess()
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        result: List<Classification>
    ): MaybeFail<Fail.Incident> {

        val tender = context.tryGetTender()
            .orReturnFail { return MaybeFail.fail(it) }

        val enrichedClassifications = result.associateBy { it }

        val updatedTenderClassification = tender.classification
            ?.let { updateTenderClassification(it, parameters.locations, enrichedClassifications) }

        val updatedItems = updateItemsClassification(tender.items, parameters.locations, enrichedClassifications)

        val updatedLots = updateLotsClassification(tender.lots, parameters.locations, enrichedClassifications)

        val updatedTender = tender.copy(
            classification = updatedTenderClassification,
            items = Items(updatedItems),
            lots = Lots(updatedLots)
        )

        context.tender = updatedTender

        return MaybeFail.none()
    }

    private fun Classification.update(classification: Classification): Classification =
         this.copy(description = classification.description)

    private fun updateTenderClassification(
        classification: Classification,
        entities: Map<EntityKey, EntityValue>,
        enrichedClassifications: Map<Classification, Classification>
    ): Classification {
        val enrichedTenderClassification = enrichedClassifications[classification]
        val updatedClassification: Classification =
            if (EntityKey.TENDER in entities && enrichedTenderClassification != null)
                classification.copy(description = enrichedTenderClassification.description)
            else
                classification

        return updatedClassification
    }

    private fun updateItemsClassification(
        items: List<Item>,
        entities: Map<EntityKey, EntityValue>,
        enrichedClassifications: Map<Classification, Classification>
    ): List<Item> {
        val updatedItems: List<Item> =
            if (EntityKey.ITEM in entities)
                items.map { item ->
                    val currentClassification = item.classification ?: return@map item
                    val enrichedItemClassification = enrichedClassifications[currentClassification]

                    enrichedItemClassification
                        ?.let { item.copy(classification = currentClassification.update(it)) }
                        ?: item
                }
            else
                items

        return updatedItems
    }

    private fun updateLotsClassification(
        lots: List<Lot>,
        entities: Map<EntityKey, EntityValue>,
        enrichedClassifications: Map<Classification, Classification>
    ): List<Lot> {
        val updatedLots: List<Lot> =
            if (EntityKey.LOT in entities)
                lots.map { lot ->
                    val currentClassification = lot.classification ?: return@map lot
                    val enrichedItemClassification = enrichedClassifications[currentClassification]

                    enrichedItemClassification
                        ?.let { lot.copy(classification = currentClassification.update(it)) }
                        ?: lot
                }
            else
                lots

        return updatedLots
    }

    private fun handleResult(
        result: GetClassification.Result,
        executionInterceptor: ExecutionInterceptor
    ): Option<Classification> = when (result) {
        is GetClassification.Result.Success -> Option.pure(
            Classification(id = result.id, scheme = result.scheme, description = result.description)
        )
        is GetClassification.Result.Fail.SchemeNotFound -> Option.none()
        is GetClassification.Result.Fail.IdNotFound -> {
            val errors = result.details.errors.convertErrors()
            executionInterceptor.throwError(errors = errors)
        }
        is GetClassification.Result.Fail.LanguageNotFound -> {
            val errors = result.details.errors.convertErrors()
            executionInterceptor.throwError(errors = errors)
        }
        is GetClassification.Result.Fail.AnotherError -> {
            val errors = result.details.errors.convertErrors()
            executionInterceptor.throwError(errors = errors)
        }
        is GetClassification.Result.Fail.TranslationNotFound -> {
            executionInterceptor.throwIncident(
                Incident(
                    id = executionInterceptor.processInstanceId,
                    date = nowDefaultUTC().format(),
                    level = Fail.Incident.Level.ERROR.toString(),
                    service = GlobalProperties.service
                        .let { service ->
                            Incident.Service(
                                id = service.id,
                                name = service.name,
                                version = service.version
                            )
                        },
                    details = result.details.errors
                        .map { incident ->
                            Incident.Detail(
                                code = incident.code,
                                description = incident.description
                            )
                        }
                )
            )
        }
    }

    private fun List<EnrichClassificationsAction.Response.Error.Details>.convertErrors(): List<Errors.Error> =
        this.map { error ->
            Errors.Error(code = error.code, description = error.description)
        }

    private fun getTenderClassification(
        tender: Tender,
        entities: Map<EntityKey, EntityValue>
    ): Result<Classification?, Fail.Incident> =
        when (entities[EntityKey.TENDER]) {
            EntityValue.OPTIONAL -> getTenderClassificationOptional(tender)
            EntityValue.REQUIRED -> getTenderClassificationRequired(tender)
            null -> null.asSuccess()
        }

    private fun getTenderClassificationOptional(tender: Tender): Result<Classification?, Fail.Incident> =
        success(tender.classification)

    private fun getTenderClassificationRequired(tender: Tender): Result<Classification?, Fail.Incident> {
        return tender.classification
            ?.asSuccess()
            ?: failure(Fail.Incident.Bpms.Context.Missing(name = "tender.classification"))
    }

    private fun getItemsClassifications(
        tender: Tender,
        entities: Map<EntityKey, EntityValue>
    ): Result<List<Classification>, Fail.Incident> =
        when (entities[EntityKey.ITEM]) {
            EntityValue.OPTIONAL -> getItemsClassificationsOptional(tender)
            EntityValue.REQUIRED -> getItemsClassificationsRequired(tender)
            null -> emptyList<Classification>().asSuccess()
        }

    private fun getItemsClassificationsOptional(tender: Tender): Result<List<Classification>, Fail.Incident> =
        tender.items
            .mapNotNull { item -> item.classification }
            .asSuccess()

    private fun getItemsClassificationsRequired(tender: Tender): Result<List<Classification>, Fail.Incident> {
        return tender.getItemsIfNotEmpty()
            .orForwardFail { fail -> return fail }
            .map { item -> item.classification!! }
            .asSuccess()
    }

    private fun getLotsClassifications(
        tender: Tender,
        entities: Map<EntityKey, EntityValue>
    ): Result<List<Classification>, Fail.Incident> =
        when (entities[EntityKey.LOT]) {
            EntityValue.OPTIONAL -> getLotsClassificationsOptional(tender)
            EntityValue.REQUIRED -> getLotsClassificationsRequired(tender)
            null -> emptyList<Classification>().asSuccess()
        }

    private fun getLotsClassificationsOptional(tender: Tender): Result<List<Classification>, Fail.Incident> =
        tender.lots
            .mapNotNull { lot -> lot.classification }
            .asSuccess()

    private fun getLotsClassificationsRequired(tender: Tender): Result<List<Classification>, Fail.Incident> {
        return tender.getLotsIfNotEmpty()
            .orForwardFail { fail -> return fail }
            .map { lot -> lot.classification!! }
            .asSuccess()
    }

    data class Parameters(val locations: Map<EntityKey, EntityValue>)

    enum class EntityKey(override val key: String) : EnumElementProvider.Key {
        TENDER("tender"),
        ITEM("item"),
        LOT("lot");

        override fun toString(): String = key

        companion object : EnumElementProvider<EntityKey>(info = info())
    }

    enum class EntityValue(override val key: String) : EnumElementProvider.Key {

        REQUIRED("required"),
        OPTIONAL("optional");

        override fun toString(): String = key

        companion object : EnumElementProvider<EntityValue>(info = info())
    }
}

