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
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.AccessCommands
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetMainProcurementCategoryAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class AccessGetRelatedTenderMainProcurementCategoryDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, GetMainProcurementCategoryAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<GetMainProcurementCategoryAction.Result>, Fail.Incident> {

        val relatedProcess = context.processInfo.relatedProcess!!
        return accessClient.getMainProcurementCategory(
            id = commandId,
            params = GetMainProcurementCategoryAction.Params(
                cpid = relatedProcess.cpid,
                ocid = relatedProcess.ocid!!
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<GetMainProcurementCategoryAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.ACCESS,
                    action = AccessCommands.GetMainProcurementCategory
                )
            )
        val receivedCategory = data.tender.mainProcurementCategory
        context.tender = context.tender?.copy(mainProcurementCategory = receivedCategory)
            ?: Tender(mainProcurementCategory = receivedCategory)

        return MaybeFail.none()
    }
}
