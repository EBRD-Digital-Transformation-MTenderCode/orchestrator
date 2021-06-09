package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.AccessCommands
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetDataForContractAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.toTenderObject
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName

class AccessGetDataForContractDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, GetDataForContractAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {
    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        Unit.asSuccess()

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<GetDataForContractAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        return accessClient.getDataForContract(
            id = commandId,
            params = GetDataForContractAction.Params(
                relatedCpid = processInfo.relatedProcess!!.cpid,
                relatedOcid = processInfo.relatedProcess.ocid!!,
                awards = context.awards.map { award ->
                    GetDataForContractAction.Params.Award(
                        award.relatedLots
                    )
                }
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<GetDataForContractAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.ACCESS,
                    action = AccessCommands.GetDataForContract
                )
            )

        context.tender = data.toTenderObject()

        return MaybeFail.none()
    }
}