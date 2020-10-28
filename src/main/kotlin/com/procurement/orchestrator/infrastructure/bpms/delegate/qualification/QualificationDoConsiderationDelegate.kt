package com.procurement.orchestrator.infrastructure.bpms.delegate.qualification

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.QualificationClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getQualificationIfOnlyOne
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.qualification.Qualifications
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.qualification.QualificationCommands
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.DoConsiderationAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class QualificationDoConsiderationDelegate(
    logger: Logger,
    private val qualificationClient: QualificationClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, DoConsiderationAction.Result>(
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
    ): Result<Reply<DoConsiderationAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        val qualification = context.getQualificationIfOnlyOne()
            .orForwardFail { fail -> return fail }

        return qualificationClient.doConsideration(
            id = commandId,
            params = DoConsiderationAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                qualifications = listOf(DoConsiderationAction.Params.Qualification(qualification.id))
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<DoConsiderationAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.QUALIFICATION,
                    action = QualificationCommands.DoConsideration
                )
            )

        val qualification = context.getQualificationIfOnlyOne()
            .orReturnFail { error -> return MaybeFail.fail(error) }
        val updatedQualification = qualification.copy(statusDetails = data.qualifications.first().statusDetails)

        context.qualifications = Qualifications(listOf(updatedQualification))

        return MaybeFail.none()
    }
}
