package com.procurement.orchestrator.infrastructure.bpms.delegate.requisition

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.RequisitionClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.model.context.members.Outcomes
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.contract.RelatedProcesses
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.item.Items
import com.procurement.orchestrator.domain.model.lot.Lots
import com.procurement.orchestrator.domain.model.tender.ProcurementMethodModalities
import com.procurement.orchestrator.domain.model.tender.auction.ElectronicAuctions
import com.procurement.orchestrator.domain.model.tender.auction.ElectronicAuctionsDetails
import com.procurement.orchestrator.domain.model.tender.conversion.Conversions
import com.procurement.orchestrator.domain.model.tender.criteria.Criteria
import com.procurement.orchestrator.domain.model.tender.target.Targets
import com.procurement.orchestrator.domain.util.extension.asList
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.requisition.RequisitionCommands
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.CreatePcrAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class RequisitionCreatePcrDelegate(
    logger: Logger,
    private val requisitionClient: RequisitionClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<RequisitionCreatePcrDelegate.Parameters, CreatePcrAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        private const val STATE = "state"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val stateFE = parameterContainer.getString(STATE)
            .orForwardFail { fail -> return fail }

        return success(Parameters(stateFE))
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<CreatePcrAction.Result>, Fail.Incident> {

        val relatedProcess = context.processInfo.relatedProcess
        val requestInfo = context.requestInfo

        val tender = context.tryGetTender()
            .orForwardFail { fail -> return fail }

        return requisitionClient.createPcr(
            id = commandId,
            params = CreatePcrAction.Params(
                cpid = relatedProcess?.cpid,
                date = requestInfo.timestamp,
                owner = requestInfo.owner,
                stateFE = parameters.state,
                tender = CreatePcrAction.RequestConverter.fromDomain(tender)
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        result: Option<CreatePcrAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.REQUISITION,
                    action = RequisitionCommands.CreatePcr
                )
            )

        val processInfo = context.processInfo
        val tender = context.tryGetTender()
            .orReturnFail { return MaybeFail.fail(it) }

        val updatedProcessInfo = processInfo.copy(ocid = data.ocid)

        context.processInfo = updatedProcessInfo

        context.relatedProcesses = data.relatedProcesses
            .map { CreatePcrAction.ResponseConverter.RelatedProcess.toDomain(it) }
            .let { RelatedProcesses(it) }

        val receivedElectronicAuctions = data.tender.electronicAuctions?.details
            ?.map { CreatePcrAction.ResponseConverter.ElectronicAuctionsDetails.toDomain(it) }
            ?.let { ElectronicAuctions(ElectronicAuctionsDetails(it)) }

        val updatedElectronicAuctions = if (receivedElectronicAuctions != null) {
            tender.electronicAuctions?.updateBy(receivedElectronicAuctions) ?: receivedElectronicAuctions
        } else
            tender.electronicAuctions

        context.tender = tender.copy(
            id = data.tender.id,
            status = data.tender.status,
            statusDetails = data.tender.statusDetails,
            date = data.tender.date,
            title = data.tender.title,
            description = data.tender.description,
            classification = CreatePcrAction.ResponseConverter.Classifications.toDomain(data.tender.classification),
            awardCriteria = data.tender.awardCriteria,
            awardCriteriaDetails = data.tender.awardCriteriaDetails,
            lots = data.tender.lots
                .map { CreatePcrAction.ResponseConverter.Lots.toDomain(it) }
                .let { Lots(it) },
            items = data.tender.items.orEmpty()
                .map { CreatePcrAction.ResponseConverter.Items.toDomain(it) }
                .let { Items(it) },
            targets = data.tender.targets.orEmpty()
                .map { CreatePcrAction.ResponseConverter.Targets.toDomain(it) }
                .let { Targets(it) },
            criteria = data.tender.criteria.orEmpty()
                .map { CreatePcrAction.ResponseConverter.Criteria.toDomain(it) }
                .let { Criteria(it) },
            conversions = data.tender.conversions.orEmpty()
                .map { CreatePcrAction.ResponseConverter.Conversions.toDomain(it) }
                .let { Conversions(it) },
            procurementMethodModalities = ProcurementMethodModalities(data.tender.procurementMethodModalities.orEmpty()),
            documents = data.tender.documents.orEmpty()
                .map { CreatePcrAction.ResponseConverter.Documents.toDomain(it) }
                .let { Documents(it) },
            electronicAuctions = updatedElectronicAuctions
        )

        context.outcomes = createOutcomes(context, data)

        return MaybeFail.none()
    }

    private fun createOutcomes(context: CamundaGlobalContext, data: CreatePcrAction.Result): Outcomes {
        val platformId = context.requestInfo.platformId
        val outcomes = context.outcomes ?: Outcomes()
        val details = outcomes[platformId] ?: Outcomes.Details()

        val newOutcomes = data
            .let { pcr -> Outcomes.Details.PreAwardCatalogRequest(id = pcr.ocid, token = pcr.token) }
            .asList()

        val updatedDetails = details.copy(pcr = newOutcomes)
        outcomes[platformId] = updatedDetails
        return outcomes
    }

    class Parameters(val state: String)
}
