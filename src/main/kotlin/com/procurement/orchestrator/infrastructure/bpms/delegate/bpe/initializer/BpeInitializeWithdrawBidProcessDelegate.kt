package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer

import com.procurement.orchestrator.application.model.context.CamundaContext
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.repository.ProcessInitializerRepository
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.fail.error.DataValidationErrors
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.model.award.AwardId
import com.procurement.orchestrator.domain.model.bid.Bid
import com.procurement.orchestrator.domain.model.bid.BidId
import com.procurement.orchestrator.domain.model.bid.Bids
import com.procurement.orchestrator.domain.model.bid.BidsDetails
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.springframework.stereotype.Component

@Component
class BpeInitializeWithdrawBidProcessDelegate(
    val logger: Logger,
    transform: Transform,
    operationStepRepository: OperationStepRepository,
    processInitializerRepository: ProcessInitializerRepository
) : AbstractInitializeProcessDelegate(logger, transform, operationStepRepository, processInitializerRepository) {

    override fun updateGlobalContext(
        camundaContext: CamundaContext,
        globalContext: CamundaGlobalContext
    ): MaybeFail<Fail> {
        val id = camundaContext.request.id!!
        val bidId = BidId.tryCreateOrNull(id)
        ?: return MaybeFail.fail(
            DataValidationErrors.DataFormatMismatch(
                name = "bidId",
                actualValue = id,
                expectedFormat = AwardId.pattern
            )
        )

        globalContext.bids = Bids(details = BidsDetails(listOf(Bid(id = bidId))))

        return MaybeFail.none()
    }
}
