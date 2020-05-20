package com.procurement.orchestrator.infrastructure.bpms.delegate.revision

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.RevisionClient
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getAmendmentIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
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
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.amendment.AmendmentId
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.revision.action.CheckAccessToAmendmentAction
import org.springframework.stereotype.Component

@Component
class RevisionCheckAccessToAmendmentDelegate(
    logger: Logger,
    private val revisionClient: RevisionClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<RevisionCheckAccessToAmendmentDelegate.Parameters, Unit>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        private const val PARAMETER_NAME_LOCATION = "location"
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

        return success(Parameters(location = location))
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<Unit>, Fail.Incident> {

        val processInfo = context.processInfo
        val cpid: Cpid = processInfo.cpid
        val ocid: Ocid = processInfo.ocid

        val tender = context.tryGetTender()
            .orForwardFail { fail -> return fail }

        val amendment = when (parameters.location) {
            Location.TENDER -> tender.getAmendmentIfOnlyOne()
                .orForwardFail { fail -> return fail }
        }

        val requestInfo = context.requestInfo
        val token: Token = requestInfo.token
            ?: return failure(Fail.Incident.Bpms.Context.Missing(name = "token", path = "requestInfo"))
        val owner: Owner = requestInfo.owner

        return revisionClient.checkAccessToAmendment(
            id = commandId,
            params = CheckAccessToAmendmentAction.Params(
                cpid = cpid,
                ocid = ocid,
                token = token,
                owner = owner,
                id = amendment.id as AmendmentId.Permanent
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        result: Option<Unit>
    ): MaybeFail<Fail.Incident> = MaybeFail.none()

    class Parameters(
        val location: Location
    )

    enum class Location(override val key: String) : EnumElementProvider.Key {

        TENDER("tender");

        override fun toString(): String = key

        companion object : EnumElementProvider<Location>(info = info())
    }
}
