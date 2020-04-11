package com.procurement.orchestrator.infrastructure.bpms.delegate.revision

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.RevisionClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.amendment.AmendmentId
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.revision.action.GetMainPartOfAmendmentByIdsAction
import org.springframework.stereotype.Component

@Component
class RevisionGetMainPartOfAmendmentByIdsDelegate(
    logger: Logger,
    private val client: RevisionClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<RevisionGetMainPartOfAmendmentByIdsDelegate.Parameters, GetMainPartOfAmendmentByIdsAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        private const val PARAMETER_NAME_LOCATION = "location"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val location: Location = parameterContainer
            .getString(
                PARAMETER_NAME_LOCATION
            )
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

        return success(Parameters(location = location))
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<GetMainPartOfAmendmentByIdsAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val cpid = processInfo.cpid
        val ocid = processInfo.ocid

        val ids: List<AmendmentId.Permanent> = when (parameters.location) {
            Location.TENDER -> context.tryGetTender()
                .orForwardFail { fail -> return fail }
                .amendments
                .map { it.id as AmendmentId.Permanent }
        }

        return client.getMainPartOfAmendmentByIds(
            id = commandId,
            params = GetMainPartOfAmendmentByIdsAction.Params(cpid = cpid, ocid = ocid, ids = ids)
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        data: GetMainPartOfAmendmentByIdsAction.Result
    ): MaybeFail<Fail.Incident> {

        when (parameters.location) {
            Location.TENDER -> {
                val amendmentByIds = data.associateBy { it.id }
                val tender = context.tryGetTender()
                    .orReturnFail { return MaybeFail.fail(it) }

                val updatedAmendments = tender.amendments
                    .map { amendment ->
                        amendmentByIds[amendment.id]
                            ?.let {
                                amendment.copy(
                                    type = it.type,
                                    status = it.status,
                                    relatesTo = it.relatesTo,
                                    relatedItem = it.relatedItem
                                )
                            }
                            ?: amendment
                    }
                val updatedTender = tender.copy(
                    amendments = updatedAmendments
                )
                context.tender = updatedTender
            }
        }
        return MaybeFail.none()
    }

    class Parameters(
        val location: Location
    )

    enum class Location(override val key: String) : EnumElementProvider.Key {

        TENDER("tender");

        override fun toString(): String = key

        companion object : EnumElementProvider<Location>(info = info())
    }
}
