package com.procurement.orchestrator.infrastructure.bpms.delegate.revision

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.RevisionClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getAmendmentIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.model.amendment.AmendmentId
import com.procurement.orchestrator.domain.model.amendment.AmendmentStatus
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.revision.action.SetStateForAmendmentAction
import org.springframework.stereotype.Component

@Component
class RevisionSetStateForAmendmentDelegate(
    logger: Logger,
    private val client: RevisionClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<RevisionSetStateForAmendmentDelegate.Parameters, SetStateForAmendmentAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        private const val PARAMETER_NAME_LOCATION = "location"
        private const val PARAMETER_NAME_STATUS = "status"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val location: Location = parameterContainer
            .getString(PARAMETER_NAME_LOCATION)
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

        val status: AmendmentStatus = parameterContainer
            .getString(PARAMETER_NAME_STATUS)
            .orForwardFail { fail -> return fail }
            .let {
                AmendmentStatus.orNull(it)
                    ?: return failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = PARAMETER_NAME_STATUS,
                            expectedValues = AmendmentStatus.allowedElements.keysAsStrings(),
                            actualValue = it
                        )
                    )
            }

        return Result.success(Parameters(location = location, status = status))
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<SetStateForAmendmentAction.Result>, Fail.Incident> {

        val id: AmendmentId.Permanent = when (parameters.location) {
            Location.TENDER -> context.tryGetTender()
                .orForwardFail { fail -> return fail }
                .getAmendmentIfOnlyOne()
                .orForwardFail { fail -> return fail }
                .id as AmendmentId.Permanent
        }

        val processInfo = context.processInfo
        return client.setStateForAmendment(
            id = commandId,
            params = SetStateForAmendmentAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                amendment = SetStateForAmendmentAction.Params.Amendment(
                    id = id,
                    status = parameters.status
                )
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        data: SetStateForAmendmentAction.Result
    ): MaybeFail<Fail.Incident> {

        when (parameters.location) {
            Location.TENDER -> {
                val tender = context.tryGetTender()
                    .orReturnFail { return MaybeFail.fail(it) }

                val updatedAmendment = tender.getAmendmentIfOnlyOne()
                    .orReturnFail { return MaybeFail.fail(it) }
                    .takeIf { it.id == data.id }
                    ?.copy(status = data.status)
                    ?: return MaybeFail.fail(
                        Fail.Incident.Bpms.Context.NotFoundElement(
                            id = data.id.toString(),
                            name = "amendments",
                            path = "tender"
                        )
                    )

                val updatedTender = tender.copy(
                    amendments = listOf(updatedAmendment)
                )
                context.tender = updatedTender
            }
        }
        return MaybeFail.none()
    }

    class Parameters(
        val location: Location,
        val status: AmendmentStatus
    )

    enum class Location(override val key: String) : EnumElementProvider.Key {

        TENDER("tender");

        override fun toString(): String = key

        companion object : EnumElementProvider<Location>(info = info())
    }
}
