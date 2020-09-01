package com.procurement.orchestrator.application.service.confirmation

import com.procurement.orchestrator.application.model.RequestId
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.container.DefaultPropertyContainer
import com.procurement.orchestrator.application.model.context.members.ProcessInfo
import com.procurement.orchestrator.application.model.context.members.RequestInfo
import com.procurement.orchestrator.application.model.process.LatestProcessContext
import com.procurement.orchestrator.application.repository.RuleRepository
import com.procurement.orchestrator.application.service.ProcessService
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.extension.date.nowDefaultUTC
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.fail.error.BpeErrors
import com.procurement.orchestrator.domain.fail.error.RequestErrors
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.amendment.Amendment
import com.procurement.orchestrator.domain.model.amendment.Amendments
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.infrastructure.bpms.repository.RequestRecord
import com.procurement.orchestrator.infrastructure.bpms.repository.RequestRepository

interface ConfirmationService {
    fun confirmAmendment(request: ConfirmationAmendment.Request): MaybeFail<Fail>
}

class ConfirmationServiceImpl(
    private val transform: Transform,
    private val processService: ProcessService,
    private val requestRepository: RequestRepository,
    private val ruleRepository: RuleRepository
) : ConfirmationService {

    companion object {
        private const val CONFIRM_AMENDMENT_PROCESS = "tenderOrLotAmendmentConfirmation"
    }

    override fun confirmAmendment(request: ConfirmationAmendment.Request): MaybeFail<Fail> {
        val savedRequest: RequestRecord = saveRequest(request)
            .orReturnFail { return MaybeFail.fail(it) }

        val isLaunched = processService.isLaunchedProcess(operationId = request.operationId)
            .orReturnFail { return MaybeFail.fail(it) }
        if (isLaunched)
            return MaybeFail.fail(RequestErrors.Common.Repeated())

        val prevProcessContext: LatestProcessContext = processService.getProcessContext(cpid = request.context.cpid)
            .orReturnFail { return MaybeFail.fail(it) }
            ?: return MaybeFail.fail(Fail.Incident.Bpe(description = "The process context by cpid '${request.context.cpid}' does not found."))

        val countryId = prevProcessContext.country
        val pmd = prevProcessContext.pmd

        val processDefinitionKey = processService
            .getProcessDefinitionKey(countryId = countryId, pmd = pmd, processName = CONFIRM_AMENDMENT_PROCESS)
            .orReturnFail { return MaybeFail.fail(it) }

        val prevStage = prevProcessContext.stage
        val prevPhase = prevProcessContext.phase
        val rule = ruleRepository
            .load(
                countryId = countryId,
                pmd = pmd,
                processDefinitionKey = processDefinitionKey,
                stageFrom = prevStage,
                phaseFrom = prevPhase
            )
            .orReturnFail { return MaybeFail.fail(it) }
            ?: return MaybeFail.fail(
                BpeErrors.ImpossibleOperation(
                    description = "Operation by country: '$countryId', pmd: '$pmd', process definition key: '$processDefinitionKey', stage: '$prevStage', phase: '$prevPhase' is impossible."
                )
            )

        val propertyContainer = DefaultPropertyContainer()
        CamundaGlobalContext(propertyContainer)
            .apply {
                requestInfo = RequestInfo(
                    operationId = savedRequest.operationId,
                    timestamp = savedRequest.timestamp,
                    requestId = savedRequest.requestId,
                    platformId = savedRequest.platformId,
                    country = countryId,
                    language = prevProcessContext.language,
                    token = request.context.token,
                    owner = request.context.owner
                )

                processInfo = ProcessInfo(
                    ocid = request.context.ocid,
                    cpid = request.context.cpid,
                    pmd = pmd,
                    operationType = rule.operationType,
                    stage = rule.stageTo,
                    prevStage = prevStage,
                    processDefinitionKey = processDefinitionKey,
                    phase = rule.phaseTo,
                    isAuction = prevProcessContext.isAuction,
                    mainProcurementCategory = prevProcessContext.mainProcurementCategory,
                    awardCriteria = prevProcessContext.awardCriteria,
                    relatedProcess = prevProcessContext.relatedProcess
                        ?.let { relatedProcess ->
                            ProcessInfo.RelatedProcess(
                                cpid = relatedProcess.cpid,
                                ocid = relatedProcess.ocid
                            )
                        }
                )

                tender = Tender(
                    amendments = Amendments(
                        Amendment(
                            id = request.context.amendmentId
                        )
                    )
                )
            }

        val variables = propertyContainer.toMap()
        processService.launchProcess(processDefinitionKey, request.operationId, variables)
        return MaybeFail.none()
    }

    private fun saveRequest(request: ConfirmationAmendment.Request): Result<RequestRecord, Fail.Incident> {
        val record = request.asRecord()
            .orForwardFail { fail -> return fail }
        requestRepository.save(record)
            .doOnError { return failure(it) }
        return success(record)
    }

    private fun ConfirmationAmendment.Request.asRecord(): Result<RequestRecord, Fail.Incident> {
        val serializedContext: String = transform.trySerialization(this.context)
            .orForwardFail { fail -> return fail }
        return RequestRecord(
            operationId = this.operationId,
            timestamp = nowDefaultUTC(),
            requestId = RequestId.generate(),
            platformId = this.platformId,
            context = serializedContext,
            payload = ""
        ).asSuccess()
    }
}
