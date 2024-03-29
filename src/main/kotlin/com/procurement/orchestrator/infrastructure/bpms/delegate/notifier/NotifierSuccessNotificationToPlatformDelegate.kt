package com.procurement.orchestrator.infrastructure.bpms.delegate.notifier

import com.procurement.orchestrator.application.client.NotificatorClient
import com.procurement.orchestrator.application.model.PlatformId
import com.procurement.orchestrator.application.model.ResponseId
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.members.Outcomes
import com.procurement.orchestrator.application.model.context.members.ProcessInfo
import com.procurement.orchestrator.application.model.context.members.RequestInfo
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractInternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.configuration.property.UriProperties
import org.springframework.stereotype.Component

@Component
class NotifierSuccessNotificationToPlatformDelegate(
    logger: Logger,
    private val platformNotificatorClient: NotificatorClient<PlatformNotification.MessageEnvelop>,
    operationStepRepository: OperationStepRepository,
    private val transform: Transform,
    uriProperties: UriProperties
) : AbstractInternalDelegate<Unit, Unit>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    private val budgetUri = uriProperties.tender
        .let { uri ->
            if (uriProperties.tender.endsWith("/"))
                uri.substring(0, uri.length - 1)
            else
                uri
        }

    private val tenderUri = uriProperties.tender
        .let { uri ->
            if (uriProperties.tender.endsWith("/"))
                uri.substring(0, uri.length - 1)
            else
                uri
        }

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(context: CamundaGlobalContext, parameters: Unit): Result<Option<Unit>, Fail.Incident> {
        buildMessages(context)
            .orForwardFail { fail -> return fail }
            .forEach { message ->
                platformNotificatorClient.send(message)
                    .doOnFail { return failure(it) }
            }

        return success(Option.none())
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        data: Unit
    ): MaybeFail<Fail.Incident.Bpmn> = MaybeFail.none()

    private fun buildMessages(context: CamundaGlobalContext): Result<List<PlatformNotification.MessageEnvelop>, Fail.Incident> {
        val requestInfo = context.requestInfo
        val processInfo = context.processInfo
        val outcomes = context.outcomes
        val platformIds: Set<PlatformId> = (outcomes?.keys ?: emptySet<PlatformId>()) + requestInfo.platformId

        return platformIds
            .map { platformId ->
                buildMessage(
                    platformId = platformId,
                    processInfo = processInfo,
                    requestInfo = requestInfo,
                    outcomeDetails = outcomes?.get(platformId)
                ).orForwardFail { fail -> return fail }
            }
            .asSuccess<List<PlatformNotification.MessageEnvelop>, Fail.Incident>()
    }

    private fun buildMessage(
        platformId: PlatformId,
        processInfo: ProcessInfo,
        requestInfo: RequestInfo,
        outcomeDetails: Outcomes.Details?
    ): Result<PlatformNotification.MessageEnvelop, Fail.Incident> {
        val message = PlatformNotification.Message.Success(
            responseId = ResponseId.generate(),
            operationId = requestInfo.operationId,
            initiator = initiator(processInfo.operationType),
            data = PlatformNotification.Message.Success.Data(
                ocid = processInfo.ocid!!,
                url = generateUrl(processInfo.operationType, cpid = processInfo.cpid!!, ocid = processInfo.ocid),
                operationDate = requestInfo.timestamp,
                outcomes = outcomeDetails?.let { buildOutcomes(it) }
            )
        ).let {
            transform.trySerialization(it)
                .orForwardFail { fail -> return fail }
        }

        return PlatformNotification.MessageEnvelop(
            platformId = platformId,
            operationId = requestInfo.operationId,
            message = message
        ).asSuccess()
    }

    private fun buildOutcomes(details: Outcomes.Details) = PlatformNotification.Outcomes(
        amendments = details.amendments
            .map { amendment ->
                PlatformNotification.Outcomes.Amendment(
                    id = amendment.id,
                    token = amendment.token
                )
            },
        awards = details.awards
            .map { award ->
                PlatformNotification.Outcomes.Award(
                    id = award.id,
                    token = award.token
                )
            },
        submissions = details.submissions
            .map { submission ->
                PlatformNotification.Outcomes.Submission(
                    id = submission.id,
                    token = submission.token
                )
            },
        qualifications = details.qualifications
            .map { qualification ->
                PlatformNotification.Outcomes.Qualification(
                    id = qualification.id,
                    token = qualification.token
                )
            },
        pcr = details.pcr
            .map { preAwardCatalogRequest ->
                PlatformNotification.Outcomes.PreAwardCatalogRequest(
                    id = preAwardCatalogRequest.id,
                    token = preAwardCatalogRequest.token
                )
            },
        bids = details.bids?.details.orEmpty()
            .map { bidDetails ->
                PlatformNotification.Outcomes.Bid(
                    id = bidDetails.id,
                    token = bidDetails.token
                )
            },
        contracts = details.contracts
            .map { contract ->
                PlatformNotification.Outcomes.Contract(
                    id = contract.id,
                    token = contract.token
                )
            },
        rq = details.rq
            .map { rq ->
                PlatformNotification.Outcomes.RequestQuotation(
                    id = rq.id
                )
            },
        requests = details.requests
            .map { request ->
                PlatformNotification.Outcomes.Request(
                    id = request.id,
                    token = request.token
                )
            },
        confirmationResponses = details.confirmationResponses
            .map { confirmationResponse ->
                PlatformNotification.Outcomes.ConfirmationResponse(
                    id = confirmationResponse.id
                )
            },
        ac = details.ac.map { ac ->
            PlatformNotification.Outcomes.AC(
                id = ac.id,
                token = ac.token
            )
        },
        po = details.po.map { po ->
            PlatformNotification.Outcomes.PO(
                id = po.id,
                token = po.token
            )
        }
    )

    private fun generateUrl(operationType: OperationTypeProcess, cpid: Cpid, ocid: Ocid): String =
        when (operationType) {
            OperationTypeProcess.ADD_GENERATED_DOCUMENT -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.APPLY_CONFIRMATIONS -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.APPLY_QUALIFICATION_PROTOCOL -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.AWARD_CONSIDERATION -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.COMPLETE_QUALIFICATION -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.COMPLETE_SOURCING -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.CREATE_AWARD -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.CREATE_CONFIRMATION_RESPONSE_BY_BUYER -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.CREATE_CONFIRMATION_RESPONSE_BY_INVITED_CANDIDATE -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.CREATE_CONFIRMATION_RESPONSE_BY_SUPPLIER -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.CREATE_CONTRACT -> "$tenderUri/$cpid"
            OperationTypeProcess.CREATE_PCR -> "$tenderUri/$cpid"
            OperationTypeProcess.CREATE_RFQ -> "$tenderUri/$cpid"
            OperationTypeProcess.CREATE_SUBMISSION -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.DECLARE_NON_CONFLICT_OF_INTEREST -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.DIVIDE_LOT -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.ISSUING_FRAMEWORK_CONTRACT -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.LOT_CANCELLATION -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.NEXT_STEP_AFTER_BUYERS_CONFIRMATION -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.NEXT_STEP_AFTER_INVITED_CANDIDATES_CONFIRMATION -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.NEXT_STEP_AFTER_SUPPLIERS_CONFIRMATION -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.OUTSOURCING_PN -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.PCR_PROTOCOL -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.QUALIFICATION -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.QUALIFICATION_CONSIDERATION -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.QUALIFICATION_DECLARE_NON_CONFLICT_OF_INTEREST -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.QUALIFICATION_PROTOCOL -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.RELATION_AP -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.START_AWARD_PERIOD -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.START_SECOND_STAGE -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.SUBMISSION_PERIOD_END -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.SUBMIT_BID -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.SUBMIT_BID_IN_PCR -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.TENDER_CANCELLATION -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.TENDER_OR_LOT_AMENDMENT_CANCELLATION -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.TENDER_OR_LOT_AMENDMENT_CONFIRMATION -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.UPDATE_AWARD -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.WITHDRAW_BID -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.WITHDRAW_PCR_PROTOCOL -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.WITHDRAW_QUALIFICATION_PROTOCOL -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.WITHDRAW_SUBMISSION -> "$tenderUri/$cpid/$ocid"
        }
}
