package com.procurement.orchestrator.infrastructure.bpms.delegate.qualification

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.QualificationClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetToken
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckAccessToQualificationAction
import org.springframework.stereotype.Component

@Component
class QualificationCheckAccessToQualificationDelegate(
    logger: Logger,
    private val client: QualificationClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, Unit>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {
    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        Result.success(Unit)

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<Unit>, Fail.Incident> {

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo

        val qualification = context.qualifications
            .first()

        val token = requestInfo.tryGetToken()
            .orForwardFail { fail -> return fail }

        return client.checkAccessToQualification(
            id = commandId,
            params = CheckAccessToQualificationAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                owner = requestInfo.owner,
                token = token,
                qualificationId = qualification.id
            )
        )
    }
}
