package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.action.ValidateRelatedTenderClassificationAction
import org.springframework.stereotype.Component

@Component
class AccessValidateRelatedTenderClassificationDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, Unit>(
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
    ): Result<Reply<Unit>, Fail.Incident> {

        val relatedProcess = context.processInfo.relatedProcess!!
        val tender = context.tryGetTender()
            .orForwardFail { failure -> return failure }

        return accessClient.validateRelatedTenderClassification(
            id = commandId,
            params = ValidateRelatedTenderClassificationAction.Params(
                cpid = relatedProcess.cpid,
                ocid = relatedProcess.ocid,
                tender = ValidateRelatedTenderClassificationAction.Params.Tender(
                    classification = tender.classification?.let { tenderClassification ->
                        ValidateRelatedTenderClassificationAction.Params.Tender.Classification(
                            id = tenderClassification.id
                        )
                    }
                )

            )
        )
    }
}
