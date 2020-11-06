package com.procurement.orchestrator.infrastructure.bpms.delegate.auction

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AuctionClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.reply.ReplyId
import com.procurement.orchestrator.infrastructure.client.web.auction.AuctionCommands
import com.procurement.orchestrator.infrastructure.client.web.auction.action.ValidateAuctionsDataAction
import org.springframework.stereotype.Component

@Component
class AuctionValidateAuctionsDataDelegate(
    logger: Logger,
    private val auctionClient: AuctionClient,
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

        val tender = context.tryGetTender()
            .orForwardFail { fail -> return fail }

        val electronicAuctionIsAbsent = tender.procurementMethodModalities.none { it == "electronicAuction" }

        if (electronicAuctionIsAbsent)
            return success(
                Reply.Success(
                    id = ReplyId.fromString(commandId.toString()),
                    version = AuctionCommands.ValidateAuctionsData.version,
                    result = Option.none()
                )
            )

        return auctionClient.validateAuctionsData(
            id = commandId,
            params = ValidateAuctionsDataAction.Params(
                tender = ValidateAuctionsDataAction.Params.Tender(
                    electronicAuctions = ValidateAuctionsDataAction.Params.Tender.ElectronicAuctions(
                        details = tender.electronicAuctions?.details?.map { detail ->
                            ValidateAuctionsDataAction.Params.Tender.ElectronicAuctions.Detail(
                                id = detail.id.toString(),
                                relatedLot = detail.relatedLot,
                                electronicAuctionModalities = detail.electronicAuctionModalities.map { modality ->
                                    ValidateAuctionsDataAction.Params.Tender.ElectronicAuctions.Detail.ElectronicAuctionModality(
                                        ValidateAuctionsDataAction.Params.Tender.ElectronicAuctions.Detail.ElectronicAuctionModality.EligibleMinimumDifference(
                                            currency = modality.eligibleMinimumDifference?.currency
                                        )
                                    )
                                }
                            )
                        }
                    ),
                    value = ValidateAuctionsDataAction.Params.Tender.Value(tender.value?.currency),
                    lots = tender.lots.map { ValidateAuctionsDataAction.Params.Tender.Lot(it.id) }
                )
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<Unit>
    ): MaybeFail<Fail.Incident.Bpmn> = MaybeFail.none()
}