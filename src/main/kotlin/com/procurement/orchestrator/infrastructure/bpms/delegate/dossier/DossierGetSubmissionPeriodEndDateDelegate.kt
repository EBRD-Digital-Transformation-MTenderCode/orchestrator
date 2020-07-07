package com.procurement.orchestrator.infrastructure.bpms.delegate.dossier

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.DossierClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.qualification.PreQualification
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.dossier.DossierCommands
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.GetSubmissionPeriodEndDateAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class DossierGetSubmissionPeriodEndDateDelegate(
    logger: Logger,
    private val dossierClient: DossierClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, GetSubmissionPeriodEndDateAction.Result>(
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
    ): Result<Reply<GetSubmissionPeriodEndDateAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        return dossierClient.getSubmissionPeriodEndDate(
            id = commandId,
            params = GetSubmissionPeriodEndDateAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<GetSubmissionPeriodEndDateAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(service = ExternalServiceName.DOSSIER, action = DossierCommands.GetSubmissionPeriodEndDate)
            )

        val preQualification = context.preQualification
        val updatedPreQualification: PreQualification = if (preQualification == null)
            PreQualification(
                period = Period(
                    endDate = data.endDate
                )
            )
        else {
            val updatedPeriod = preQualification.period
                ?.copy(endDate = data.endDate)
                ?: Period(endDate = data.endDate)
            preQualification.copy(period = updatedPeriod)
        }

        context.preQualification = updatedPreQualification

        return MaybeFail.none()
    }
}
