package com.procurement.orchestrator.infrastructure.bpms.delegate.revision

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.RevisionClient
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getAmendmentIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getLotIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.model.context.members.Outcomes
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.amendment.Amendments
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.revision.RevisionCommands
import com.procurement.orchestrator.infrastructure.client.web.revision.action.CreateAmendmentAction
import org.springframework.stereotype.Component

@Component
class RevisionCreateAmendmentDelegate(
    logger: Logger,
    private val client: RevisionClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, CreateAmendmentAction.Result>(
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
    ): Result<Reply<CreateAmendmentAction.Result>, Fail.Incident> {

        val tender = context.tryGetTender()
            .orForwardFail { fail -> return fail }

        val amendment = tender.getAmendmentIfOnlyOne()
            .orForwardFail { fail -> return fail }

        val processInfo = context.processInfo
        val relatedEntityId: String = when (processInfo.operationType) {
            OperationTypeProcess.TENDER_CANCELLATION -> processInfo.ocid.toString()

            OperationTypeProcess.LOT_CANCELLATION -> tender.getLotIfOnlyOne()
                .orForwardFail { fail -> return fail }
                .id
                .toString()

            OperationTypeProcess.CREATE_SUBMISSION,
            OperationTypeProcess.DECLARE_NON_CONFLICT_OF_INTEREST,
            OperationTypeProcess.TENDER_OR_LOT_AMENDMENT_CANCELLATION,
            OperationTypeProcess.TENDER_OR_LOT_AMENDMENT_CONFIRMATION,
            OperationTypeProcess.WITHDRAW_SUBMISSION,
            OperationTypeProcess.QUALIFICATION_DECLARE_NON_CONFLICT_OF_INTEREST->
                return failure(Fail.Incident.Bpe(description = "Operation type: '${processInfo.operationType.key}' in this delegate do not implemented."))
        }

        val requestInfo = context.requestInfo
        val owner: Owner = requestInfo.owner
        return client.createAmendment(
            id = commandId,
            params = CreateAmendmentAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                owner = owner,
                operationType = processInfo.operationType,
                date = requestInfo.timestamp,
                relatedEntityId = relatedEntityId,
                amendment = CreateAmendmentAction.Params.Amendment(
                    id = amendment.id,
                    rationale = amendment.rationale,
                    description = amendment.description,
                    documents = amendment.documents
                        .map { document ->
                            CreateAmendmentAction.Params.Amendment.Document(
                                id = document.id,
                                documentType = document.documentType,
                                title = document.title,
                                description = document.description
                            )
                        }
                )
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<CreateAmendmentAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(service = "eRevision", action = RevisionCommands.CreateAmendment)
            )

        val tender = context.tryGetTender()
            .orReturnFail { return MaybeFail.fail(it) }

        val updatedAmendment = tender.getAmendmentIfOnlyOne()
            .orReturnFail { return MaybeFail.fail(it) }
            .copy(
                type = data.type,
                status = data.status,
                relatesTo = data.relatesTo,
                relatedItem = data.relatedItem,
                date = data.date
            )

        val updatedTender = tender.copy(
            amendments = Amendments(updatedAmendment)
        )
        context.tender = updatedTender

        updateOutcomes(context, data)

        return MaybeFail.none()
    }

    private fun updateOutcomes(
        context: CamundaGlobalContext,
        data: CreateAmendmentAction.Result
    ) {
        val platformId = context.requestInfo.platformId
        val outcomes = context.outcomes ?: Outcomes()
        val details = outcomes[platformId] ?: Outcomes.Details()
        val updatedDetails = details.copy(
            amendments = details.amendments + Outcomes.Details.Amendment(
                id = data.id,
                token = data.token
            )
        )
        outcomes[platformId] = updatedDetails
        context.outcomes = outcomes
    }
}
