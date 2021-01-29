package com.procurement.orchestrator.infrastructure.bpms.delegate.contracting

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.ContractingClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.party.Parties
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.FindSupplierReferencesOfActivePacsAction
import org.springframework.stereotype.Component

@Component
class ContractingFindSupplierReferencesOfActivePacsDelegate(
    logger: Logger,
    private val contractingClient: ContractingClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, FindSupplierReferencesOfActivePacsAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        private const val NAME_PARAMETER_OF_STATUS = "status"
        private const val NAME_PARAMETER_OF_STATUS_DETAILS = "statusDetails"
        private const val NAME_PARAMETER_OF_STATES = "states"
        private const val LOCATION_OF_LOTS = "locationOfLots"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<FindSupplierReferencesOfActivePacsAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        val tender = context.tryGetTender()
            .orForwardFail { incident -> return incident }

        return contractingClient.findSupplierReferencesOfActivePacs(
            id = commandId,
            params = FindSupplierReferencesOfActivePacsAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                tender = FindSupplierReferencesOfActivePacsAction.Params.Tender(
                    lots = tender.lots.map { lot ->
                        FindSupplierReferencesOfActivePacsAction.Params.Tender.Lot(
                            id = lot.id
                        )
                    }
                )

            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<FindSupplierReferencesOfActivePacsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.none()

        val parties = data.contracts
            .flatMap { it.suppliers }
            .map { FindSupplierReferencesOfActivePacsAction.toDomain(it) }
            .let { Parties(it) }

        context.parties = parties

        return MaybeFail.none()
    }

}
