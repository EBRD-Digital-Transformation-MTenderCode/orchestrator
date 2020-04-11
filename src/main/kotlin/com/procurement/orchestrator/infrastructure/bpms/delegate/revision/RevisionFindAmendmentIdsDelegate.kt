package com.procurement.orchestrator.infrastructure.bpms.delegate.revision

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.RevisionClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getLotsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.extension.amendmentIds
import com.procurement.orchestrator.domain.extension.union
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.amendment.Amendment
import com.procurement.orchestrator.domain.model.amendment.AmendmentId
import com.procurement.orchestrator.domain.model.amendment.AmendmentRelatesTo
import com.procurement.orchestrator.domain.model.amendment.AmendmentStatus
import com.procurement.orchestrator.domain.model.amendment.AmendmentType
import com.procurement.orchestrator.domain.util.extension.getNewElements
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.revision.action.FindAmendmentIdsAction
import org.springframework.stereotype.Component

@Component
class RevisionFindAmendmentIdsDelegate(
    logger: Logger,
    private val client: RevisionClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<RevisionFindAmendmentIdsDelegate.Parameters, List<AmendmentId>>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val type: AmendmentType? = parameterContainer.getStringOrNull("type")
            .orForwardFail { fail -> return fail }
            ?.let {
                AmendmentType.orNull(it)
                    ?: return failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = "type",
                            expectedValues = AmendmentType.allowedElements.keysAsStrings(),
                            actualValue = it
                        )
                    )
            }

        val status: AmendmentStatus? = parameterContainer.getStringOrNull("status")
            .orForwardFail { fail -> return fail }
            ?.let {
                AmendmentStatus.orNull(it)
                    ?: return failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = "status",
                            expectedValues = AmendmentType.allowedElements.keysAsStrings(),
                            actualValue = it
                        )
                    )
            }

        val relatesTo: AmendmentRelatesTo? = parameterContainer.getStringOrNull("relatesTo")
            .orForwardFail { fail -> return fail }
            ?.let {
                AmendmentRelatesTo.orNull(it)
                    ?: return failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = "relatesTo",
                            expectedValues = AmendmentType.allowedElements.keysAsStrings(),
                            actualValue = it
                        )
                    )
            }

        val sendRelatedItem = parameterContainer.getStringOrNull("sendRelatedItem")
            .orForwardFail { fail -> return fail }
            ?.toBoolean()
            ?: false

        return success(
            Parameters(
                type = type,
                relatesTo = relatesTo,
                status = status,
                sendRelatedItem = sendRelatedItem
            )
        )
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<List<AmendmentId>>, Fail.Incident> {

        val processInfo = context.processInfo
        val cpid = processInfo.cpid
        val ocid = processInfo.ocid

        val relatedItems: List<String> = if (parameters.sendRelatedItem)
            when (parameters.relatesTo) {
                AmendmentRelatesTo.TENDER -> listOf(ocid.toString())
                AmendmentRelatesTo.LOT -> {
                    val tender = context.tryGetTender()
                        .orForwardFail { fail -> return fail }

                    tender.getLotsIfNotEmpty()
                        .orForwardFail { fail -> return fail }
                        .map { it.id.toString() }
                }
                null -> return failure(
                    Fail.Incident.Bpmn.Parameter.UnConsistency(
                        description = "Attribute 'relatesTo' is undefined, but attribute 'sendRelatedItem' has '${parameters.sendRelatedItem}' value."
                    )
                )
            }
        else
            emptyList()

        return client.findAmendmentIds(
            id = commandId,
            params = FindAmendmentIdsAction.Params(
                cpid = cpid,
                ocid = ocid,
                type = parameters.type,
                status = parameters.status,
                relatesTo = parameters.relatesTo,
                relatedItems = relatedItems
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        data: List<AmendmentId>
    ): MaybeFail<Fail.Incident> {

        val tender = context.tryGetTender()
            .orReturnFail { return MaybeFail.fail(it) }

        val knowAmendmentIds = tender.amendmentIds()
        val receivedAmendmentIds = data.toSet()
        val newAmendments = getNewElements(received = receivedAmendmentIds, known = knowAmendmentIds)
            .map { id -> Amendment(id = id) }

        val updatedTender = tender.copy(
            amendments = tender.amendments.union(newAmendments)
        )
        context.tender = updatedTender

        return MaybeFail.none()
    }

    class Parameters(
        val type: AmendmentType?,
        val relatesTo: AmendmentRelatesTo?,
        val status: AmendmentStatus?,
        val sendRelatedItem: Boolean
    )
}
