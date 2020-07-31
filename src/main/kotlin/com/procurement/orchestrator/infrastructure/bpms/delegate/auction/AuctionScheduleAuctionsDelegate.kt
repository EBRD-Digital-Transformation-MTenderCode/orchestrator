package com.procurement.orchestrator.infrastructure.bpms.delegate.auction

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AuctionClientV1
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.period.Period
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
import com.procurement.orchestrator.infrastructure.client.web.auction.AuctionCommandsV1
import com.procurement.orchestrator.infrastructure.client.web.auction.action.ScheduleAuctionsAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class AuctionScheduleAuctionsDelegate(
    logger: Logger,
    private val auctionClient: AuctionClientV1,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, ScheduleAuctionsAction.ResponseData>(
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
    ): Result<Reply<ScheduleAuctionsAction.ResponseData>, Fail.Incident> {

        val tender = context.tryGetTender()
            .orForwardFail { fail -> return fail }

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo
        return auctionClient.scheduleAuctions(
            id = commandId,
            context = ScheduleAuctionsAction.Context(
                operationId = requestInfo.operationId,
                startDate = requestInfo.timestamp,
                cpid = processInfo.cpid,
                country = requestInfo.country,
                pmd = processInfo.pmd
            ),
            data = ScheduleAuctionsAction.Data(
                tenderPeriod = ScheduleAuctionsAction.Data.TenderPeriod(endDate = tender.tenderPeriod?.endDate),
                electronicAuctions = tender.electronicAuctions.let { electronicAuctions ->
                    ScheduleAuctionsAction.Data.ElectronicAuctions(
                        details = electronicAuctions?.details?.map { detail ->
                            ScheduleAuctionsAction.Data.ElectronicAuctions.Detail(
                                relatedLot = detail.relatedLot,
                                electronicAuctionModalities = detail.electronicAuctionModalities.map { electronicAuctionModality ->
                                    ScheduleAuctionsAction.Data.ElectronicAuctions.Detail.ElectronicAuctionModality(
                                        electronicAuctionModality.eligibleMinimumDifference.let { eligibleMinimumDifference ->
                                            ScheduleAuctionsAction.Data.ElectronicAuctions.Detail.ElectronicAuctionModality.EligibleMinimumDifference(
                                                amount = eligibleMinimumDifference?.amount,
                                                currency = eligibleMinimumDifference?.currency
                                            )
                                        }
                                    )
                                }
                            )
                        }
                    )
                }
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<ScheduleAuctionsAction.ResponseData>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.AUCTION,
                    action = AuctionCommandsV1.ScheduleAuctions
                )
            )

        val tender = context.tryGetTender()
            .orReturnFail { return MaybeFail.fail(it) }

        val auctionPeriod = Period(data.auctionPeriod.startDate)
        val electronicAuctions = convertToElectronicAuctions(data)

        val updatedTender = tender.copy(auctionPeriod = auctionPeriod, electronicAuctions = electronicAuctions)

        context.tender = updatedTender

        return MaybeFail.none()
    }

    private fun convertToElectronicAuctions(data: ScheduleAuctionsAction.ResponseData): ElectronicAuctions {
        return ElectronicAuctions(
            details = data.electronicAuctions.details
                .map { detail ->
                    ElectronicAuctionsDetail(
                        id = detail.id,
                        relatedLot = detail.relatedLot,
                        auctionPeriod = Period(startDate = detail.auctionPeriod.startDate),
                        electronicAuctionModalities = detail.electronicAuctionModalities
                            .map { electronicAuctionModality ->
                                ElectronicAuctionModality(
                                    url = electronicAuctionModality.url,
                                    eligibleMinimumDifference = electronicAuctionModality.eligibleMinimumDifference.let { eligibleMinimumDifference ->
                                        Value(
                                            amount = eligibleMinimumDifference.amount,
                                            currency = eligibleMinimumDifference.currency
                                        )
                                    }
                                )
                            }
                            .let { ElectronicAuctionModalities(it) }
                    )
                }.let { ElectronicAuctionsDetails(it) }
        )
    }
}