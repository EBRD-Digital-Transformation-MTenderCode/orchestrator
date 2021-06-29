package com.procurement.orchestrator.infrastructure.bpms.delegate.evaluation

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.EvaluationClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getContractIfNotEmpty
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.award.Award
import com.procurement.orchestrator.domain.model.award.Awards
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.lot.RelatedLots
import com.procurement.orchestrator.domain.model.organization.Organization
import com.procurement.orchestrator.domain.model.organization.Organizations
import com.procurement.orchestrator.domain.model.value.Value
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.evaluation.EvaluationCommands
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.GetRelatedAwardByIdsAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.convertToParties
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class EvaluationGetRelatedAwardByIdsDelegate(
    logger: Logger,
    private val evaluationClient: EvaluationClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, GetRelatedAwardByIdsAction.Result>(
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
    ): Result<Reply<GetRelatedAwardByIdsAction.Result>, Fail.Incident> {

        val relatedProcess = context.processInfo.relatedProcess!!

        val contracts = context.getContractIfNotEmpty()
            .orForwardFail { fail -> return fail }

        return evaluationClient.getRelatedAwardByIds(
            id = commandId,
            params = GetRelatedAwardByIdsAction.Params(
                cpid = relatedProcess.cpid,
                ocid = relatedProcess.ocid!!,
                awards = contracts.map { contract ->
                    GetRelatedAwardByIdsAction.Params.Award(
                        id = contract.awardId!!
                    )
                }
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<GetRelatedAwardByIdsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.EVALUATION,
                    action = EvaluationCommands.GetRelatedAwardByIds
                )
            )

        val updatedAward = data.awards
            .map { award ->
                Award(
                    suppliers = getSuppliersReference(award),
                    id = award.id,
                    internalId = award.internalId,
                    date = award.date,
                    status = award.status,
                    statusDetails = award.statusDetails,
                    description = award.description,
                    value = award.value.let { value ->
                        Value(
                            amount = value.amount,
                            currency = value.currency
                        )
                    },
                    relatedLots = RelatedLots(award.relatedLots),
                    documents = award.documents?.map { document ->
                        Document(
                            id = document.id,
                            title = document.title,
                            description = document.description,
                            documentType = document.documentType
                        )
                    }
                        .let { Documents(it.orEmpty()) },
                    relatedBid = award.relatedBid
                )
            }
            .let { Awards(it) }

        context.parties = data.convertToParties()
        context.awards = updatedAward

        return MaybeFail.none()
    }

    private fun getSuppliersReference(
        receivedAward: GetRelatedAwardByIdsAction.Result.Award
    ): Organizations = receivedAward.suppliers.map { supplier ->
        Organization(
            name = supplier.name,
            id = supplier.id
        )
    }
        .let { Organizations(it) }
}
