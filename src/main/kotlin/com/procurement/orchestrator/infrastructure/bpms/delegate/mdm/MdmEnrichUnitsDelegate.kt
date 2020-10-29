package com.procurement.orchestrator.infrastructure.bpms.delegate.mdm

import com.procurement.orchestrator.application.client.MdmClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getItemsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getTargetsIfNotEmpty
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
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.bid.Bids
import com.procurement.orchestrator.domain.model.bid.BidsDetails
import com.procurement.orchestrator.domain.model.contract.observation.Observations
import com.procurement.orchestrator.domain.model.item.Item
import com.procurement.orchestrator.domain.model.item.Items
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.domain.model.tender.target.Target
import com.procurement.orchestrator.domain.model.tender.target.Targets
import com.procurement.orchestrator.domain.model.unit.Unit
import com.procurement.orchestrator.domain.model.unit.UnitId
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractBatchRestDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichUnitsAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetUnit
import com.procurement.orchestrator.infrastructure.configuration.property.GlobalProperties
import org.springframework.stereotype.Component

@Component
class MdmEnrichUnitsDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    transform: Transform,
    private val mdmClient: MdmClient
) : AbstractBatchRestDelegate<MdmEnrichUnitsDelegate.Parameters, EnrichUnitsAction.Params, List<Unit>>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {
    companion object {
        const val PARAMETER_LOCATIONS = "locations"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val locations: Map<EntityKey, EntityValue> = parameterContainer.getMapString(PARAMETER_LOCATIONS)
            .orForwardFail { fail -> return fail }
            .map { (key, value) ->
                val keyParsed = EntityKey.orNull(key)
                    ?: return failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = PARAMETER_LOCATIONS,
                            expectedValues = EntityKey.allowedElements.keysAsStrings(),
                            actualValue = key
                        )
                    )
                val valueParsed = EntityValue.orNull(value)
                    ?: return failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = PARAMETER_LOCATIONS,
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
    ): Result<List<EnrichUnitsAction.Params>, Fail.Incident> {
        val requestInfo = context.requestInfo
        val entities = parameters.locations
        val tender = context.tryGetTender()
            .orForwardFail { fail -> return fail }

        val itemsUnits: List<UnitId> = getItemsUnits(tender, entities)
            .orForwardFail { fail -> return fail }

        val targetsUnits: List<UnitId> = getTargetsUnits(tender, entities)
            .orForwardFail { fail -> return fail }

        val bidsItemsUnits: List<UnitId> = getBidsItemsUnits(context, entities)
            .orForwardFail { fail -> return fail }

        val allUnits: Set<UnitId> = mutableSetOf<UnitId>()
            .apply {
                addAll(itemsUnits)
                addAll(targetsUnits)
                addAll(bidsItemsUnits)
            }

        val params = allUnits.map { unitId ->
            EnrichUnitsAction.Params(
                lang = requestInfo.language,
                unitId = unitId
            )
        }

        return success(params)
    }

    override suspend fun execute(
        elements: List<EnrichUnitsAction.Params>,
        executionInterceptor: ExecutionInterceptor
    ): Result<List<Unit>, Fail.Incident> {

        return elements
            .map { params ->
                val response = mdmClient
                    .enrichUnits(params = params)
                    .orForwardFail { error -> return error }

                handleResult(response, executionInterceptor)
            }
            .asSuccess()
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        result: List<Unit>
    ): MaybeFail<Fail.Incident> {

        val tender = context.tryGetTender()
            .orReturnFail { return MaybeFail.fail(it) }

        val enrichedUnits = result.associateBy { it }

        val updatedItems = updateItemsUnit(tender.items, parameters.locations, enrichedUnits)

        val updatedTargets = updateTargetsUnit(tender.targets, parameters.locations, enrichedUnits)

        val updatedBids = updateBidsItemsUnits(context, parameters.locations, enrichedUnits)

        val updatedTender = tender.copy(
            items = Items(updatedItems),
            targets = Targets(updatedTargets)
        )

        context.tender = updatedTender
        context.bids = updatedBids

        return MaybeFail.none()
    }

    private fun Unit.update(unit: Unit): Unit =
        this.copy(name = unit.name)

    private fun updateItemsUnit(
        items: List<Item>,
        entities: Map<EntityKey, EntityValue>,
        enrichedUnits: Map<Unit, Unit>
    ): List<Item> {
        val updatedItems: List<Item> =
            if (EntityKey.ITEM in entities)
                items.map { item ->
                    val currentUnit = item.unit
                    val enrichedItemUnit = currentUnit?.let { enrichedUnits[it] }

                    if (enrichedItemUnit != null)
                        item.copy(unit = currentUnit.update(enrichedItemUnit))
                    else
                        item
                }
            else
                items

        return updatedItems
    }

    private fun updateTargetsUnit(
        targets: List<Target>,
        entities: Map<EntityKey, EntityValue>,
        enrichedUnits: Map<Unit, Unit>
    ): List<Target> {
        val updatedTargets: List<Target> =
            if (EntityKey.TARGET in entities) {
                val updatedTargets = targets.map { target ->
                    val updatedObservation = target.observations
                        .map { observation ->
                            val currentUnit = observation.unit
                            val enrichedItemUnit = currentUnit?.let { enrichedUnits[it] }

                            if (enrichedItemUnit != null)
                                observation.copy(unit = currentUnit.update(enrichedItemUnit))
                            else
                                observation
                        }
                    target.copy(observations = Observations(updatedObservation))
                }
                updatedTargets
            } else
                targets

        return updatedTargets
    }

    private fun updateBidsItemsUnits(
        context: CamundaGlobalContext,
        entities: Map<EntityKey, EntityValue>,
        enrichedUnits: Map<Unit, Unit>
    ): Bids? {
        val bids = context.bids!!.details
        val updatedBidsDetails: BidsDetails =
            if (EntityKey.BID_ITEM in entities) {
                val updatedBids = bids.map { bid ->
                    val updatedItems = bid.items
                        .map { item ->
                            val currentUnit = item.unit
                            val enrichedItemUnit = currentUnit?.let { enrichedUnits[it] }

                            if (enrichedItemUnit != null)
                                item.copy(unit = currentUnit.update(enrichedItemUnit))
                            else
                                item
                        }
                    bid.copy(items = Items(updatedItems))
                }
                BidsDetails(updatedBids)
            } else
                bids

        return context.bids?.copy(details = updatedBidsDetails)
    }

    private fun handleResult(
        result: GetUnit.Result,
        executionInterceptor: ExecutionInterceptor
    ): Unit = when (result) {
        is GetUnit.Result.Success -> Unit(id = result.id, name = result.name)

        is GetUnit.Result.Fail.IdNotFound -> {
            val errors = result.details.errors.convertErrors()
            executionInterceptor.throwError(errors = errors)
        }
        is GetUnit.Result.Fail.LanguageNotFound -> {
            val errors = result.details.errors.convertErrors()
            executionInterceptor.throwError(errors = errors)
        }
        is GetUnit.Result.Fail.AnotherError -> {
            val errors = result.details.errors.convertErrors()
            executionInterceptor.throwError(errors = errors)
        }
        is GetUnit.Result.Fail.TranslationNotFound -> {
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

    private fun List<EnrichUnitsAction.Response.Error.Details>.convertErrors(): List<Errors.Error> =
        this.map { error ->
            Errors.Error(code = error.code, description = error.description)
        }

    private fun getItemsUnits(
        tender: Tender,
        entities: Map<EntityKey, EntityValue>
    ): Result<List<UnitId>, Fail.Incident> =
        when (entities[EntityKey.ITEM]) {
            EntityValue.OPTIONAL -> getItemsUnitsOptional(tender)
            EntityValue.REQUIRED -> getItemsUnitsRequired(tender)
            null -> emptyList<UnitId>().asSuccess()
        }

    private fun getItemsUnitsOptional(tender: Tender): Result<List<UnitId>, Fail.Incident> =
        tender.items
            .mapNotNull { item -> item.unit?.id }
            .asSuccess()

    private fun getItemsUnitsRequired(tender: Tender): Result<List<UnitId>, Fail.Incident> {
        return tender.getItemsIfNotEmpty()
            .orForwardFail { fail -> return fail }
            .map { item ->
                val unit = item.unit
                if (unit == null)
                    return failure(Fail.Incident.Bpms.Context.Missing(name = "unit", path = "#.items[id:${item.id}]"))
                else
                    unit.id ?: return failure(Fail.Incident.Bpms.Context.Empty(name = "unit.id", path = "#.items[id:${item.id}]"))
            }
            .asSuccess()
    }

    private fun getTargetsUnits(
        tender: Tender,
        entities: Map<EntityKey, EntityValue>
    ): Result<List<UnitId>, Fail.Incident> =
        when (entities[EntityKey.TARGET]) {
            EntityValue.OPTIONAL -> getTargetsUnitsOptional(tender)
            EntityValue.REQUIRED -> getTargetsUnitsRequired(tender)
            null -> emptyList<UnitId>().asSuccess()
        }

    private fun getTargetsUnitsOptional(tender: Tender): Result<List<UnitId>, Fail.Incident> =
        tender.targets
            .flatMap { target -> target.observations }
            .mapNotNull { observation -> observation.unit?.id }
            .asSuccess()

    private fun getTargetsUnitsRequired(tender: Tender): Result<List<UnitId>, Fail.Incident> {
        return tender.getTargetsIfNotEmpty()
            .orForwardFail { fail -> return fail }
            .flatMap { target -> target.observations }
            .map { observation ->
                val unit = observation.unit
                if (unit == null)
                    return failure(
                        Fail.Incident.Bpms.Context.Missing(
                            name = "unit",
                            path = "#.targets[].observation[id:${observation.id}]"
                        )
                    )
                else
                    unit.id ?: return failure(Fail.Incident.Bpms.Context.Empty(name = "unit.id", path = "#.targets[].observation[id:${observation.id}]"))
            }
            .asSuccess()
    }

    private fun getBidsItemsUnits(
        context: CamundaGlobalContext,
        entities: Map<EntityKey, EntityValue>
    ): Result<List<UnitId>, Fail.Incident> =
        when (entities[EntityKey.BID_ITEM]) {
            EntityValue.OPTIONAL -> getBidsItemsUnitsOptional(context)
            EntityValue.REQUIRED -> getBidstItemsUnitsRequired(context)
            null -> emptyList<UnitId>().asSuccess()
        }

    private fun getBidsItemsUnitsOptional(context: CamundaGlobalContext): Result<List<UnitId>, Fail.Incident> =
        context.bids?.details?.get(0)?.items
            ?.mapNotNull { item -> item.unit?.id }
            .orEmpty()
            .asSuccess()

    private fun getBidstItemsUnitsRequired(context: CamundaGlobalContext): Result<List<UnitId>, Fail.Incident> {
        val bidsItems = context.bids?.details?.get(0)?.items
            ?: return failure(Fail.Incident.Bpms.Context.Missing(name = "bids/items"))

        val unitIds = bidsItems.map { item ->
            val unit = item.unit
            if (unit == null)
                return failure(
                    Fail.Incident.Bpms.Context.Missing(
                        name = "unit",
                        path = "#.targets[].observation[id:${item.id}]"
                    )
                )
            else
                unit.id
        }

        return success(unitIds)
    }

    data class Parameters(val locations: Map<EntityKey, EntityValue>)

    enum class EntityKey(override val key: String) : EnumElementProvider.Key {
        TARGET("target"),
        BID_ITEM("bid.item"),
        ITEM("item");

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

