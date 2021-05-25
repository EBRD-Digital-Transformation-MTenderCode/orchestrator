package com.procurement.orchestrator.infrastructure.bpms.delegate.submission

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.SubmissionClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getContractIfOnlyOne
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.process.master.data.ProcessMasterData
import com.procurement.orchestrator.domain.model.process.master.data.Submission
import com.procurement.orchestrator.domain.model.process.master.data.Tenderers
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.submission.SubmissionCommands
import com.procurement.orchestrator.infrastructure.client.web.submission.action.GetSuppliersOwnersAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.toDomain
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class SubmissionGetSuppliersOwnersDelegate(
    logger: Logger,
    private val submissionClient: SubmissionClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, GetSuppliersOwnersAction.Result>(
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
    ): Result<Reply<GetSuppliersOwnersAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val contract = context.getContractIfOnlyOne().orForwardFail { return it }

        return submissionClient.getSuppliersOwners(
            id = commandId,
            params = GetSuppliersOwnersAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                contracts = listOf(
                    GetSuppliersOwnersAction.Params.Contract(
                        id = contract.id,
                        suppliers = contract.suppliers.map { supplier ->
                            GetSuppliersOwnersAction.Params.Contract.Supplier(
                                id = supplier.id
                            )
                        }
                    )
                )
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<GetSuppliersOwnersAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.SUBMISSION,
                    action = SubmissionCommands.GetSuppliersOwners
                )
            )

        val receivedTenderers = data.tenderers
            .map { it.toDomain() }
            .let { Tenderers(it) }

        context.processMasterData = ProcessMasterData(
            submission = Submission(
                tenderers = Tenderers(receivedTenderers)
            )
        )

        return MaybeFail.none()
    }
}
