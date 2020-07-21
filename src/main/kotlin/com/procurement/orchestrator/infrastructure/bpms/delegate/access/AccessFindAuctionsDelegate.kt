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
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.domain.model.tender.auction.ElectronicAuctionModalities
import com.procurement.orchestrator.domain.model.tender.auction.ElectronicAuctionModality
import com.procurement.orchestrator.domain.model.tender.auction.ElectronicAuctions
import com.procurement.orchestrator.domain.model.tender.auction.ElectronicAuctionsDetail
import com.procurement.orchestrator.domain.model.tender.auction.ElectronicAuctionsDetails
import com.procurement.orchestrator.domain.model.value.Value
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.action.FindAuctionsAction
import org.springframework.stereotype.Component

@Component
class AccessFindAuctionsDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, FindAuctionsAction.Result>(
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
    ): Result<Reply<FindAuctionsAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        return accessClient.findAuctions(
            id = commandId,
            params = FindAuctionsAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<FindAuctionsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.none()

        val receivedDetails = data.tender.electronicAuctions.details
            .map { detail ->
                ElectronicAuctionsDetail(
                    id = detail.id,
                    relatedLot = detail.relatedLot,
                    electronicAuctionModalities = ElectronicAuctionModalities(
                        detail.electronicAuctionModalities
                            .map { modality ->
                                ElectronicAuctionModality(
                                    eligibleMinimumDifference = modality.eligibleMinimumDifference
                                        .let { eligibleMinimumDifference ->
                                            Value(
                                                amount = eligibleMinimumDifference.amount,
                                                currency = eligibleMinimumDifference.currency
                                            )
                                        }
                                )
                            }
                    )
                )
            }

        val tender = context.tender ?: Tender()

        val electronicAuctions = tender.electronicAuctions ?: ElectronicAuctions()
        val updatedDetails = electronicAuctions.details updateBy ElectronicAuctionsDetails(receivedDetails)
        val updatedTender = tender.copy(electronicAuctions = electronicAuctions.copy(details = updatedDetails))

        context.tender = updatedTender

        return MaybeFail.none()
    }
}
