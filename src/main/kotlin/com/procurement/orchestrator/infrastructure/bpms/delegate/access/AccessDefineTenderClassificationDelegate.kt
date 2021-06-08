package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.classification.Classification
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.AccessCommands
import com.procurement.orchestrator.infrastructure.client.web.access.action.DefineTenderClassificationAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class AccessDefineTenderClassificationDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, DefineTenderClassificationAction.Result>(
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
    ): Result<Reply<DefineTenderClassificationAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        val tender = context.tryGetTender()
            .orForwardFail { incident -> return incident }

        return accessClient.defineTenderClassification(
            id = commandId,
            params = DefineTenderClassificationAction.Params(
                relatedCpid = processInfo.relatedProcess!!.cpid,
                relatedOcid = processInfo.relatedProcess.ocid!!,
                tender = DefineTenderClassificationAction.Params.Tender(
                    items = tender.items.map { item ->
                        DefineTenderClassificationAction.Params.Tender.Item(
                            id = item.id,
                            classification = item.classification.let { classification ->
                                DefineTenderClassificationAction.Params.Tender.Item.Classification(
                                    id = classification!!.id,
                                    scheme = classification.scheme
                                )
                            }
                        )
                    }
                )
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<DefineTenderClassificationAction.Result>
    ): MaybeFail<Fail.Incident> {
        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.ACCESS,
                    action = AccessCommands.DefineTenderClassification
                )
            )

        val tender = context.tryGetTender()
            .orForwardFail { return MaybeFail.fail(it.error) }

        val receivedClassification = data.tender.classification
        val receivedTender = Tender(
            classification = Classification(
                id = receivedClassification.id,
                scheme = receivedClassification.scheme
            )
        )

        context.tender = tender updateBy receivedTender

        return MaybeFail.none()
    }
}