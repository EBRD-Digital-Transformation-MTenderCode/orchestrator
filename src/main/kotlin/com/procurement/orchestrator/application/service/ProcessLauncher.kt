package com.procurement.orchestrator.application.service

import com.procurement.orchestrator.application.model.RequestId
import com.procurement.orchestrator.application.model.context.CamundaContext
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.container.DefaultPropertyContainer
import com.procurement.orchestrator.application.model.context.members.ProcessInfo
import com.procurement.orchestrator.application.model.context.members.RequestInfo
import com.procurement.orchestrator.application.model.process.LatestProcessContext
import com.procurement.orchestrator.application.repository.RuleRepository
import com.procurement.orchestrator.domain.extension.date.nowDefaultUTC
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.fail.error.BpeErrors
import com.procurement.orchestrator.domain.fail.error.RequestErrors
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.infrastructure.bpms.repository.RequestRecord
import com.procurement.orchestrator.infrastructure.bpms.repository.RequestRepository

interface ProcessLauncher {
    fun launch(request: PlatformRequest): MaybeFail<Fail>
    fun launch(event: ChronographEvent): MaybeFail<Fail>
}

class ProcessLauncherImpl(
    private val transform: Transform,
    private val processService: ProcessService,
    private val requestRepository: RequestRepository,
    private val ruleRepository: RuleRepository
) : ProcessLauncher {

    override fun launch(request: PlatformRequest): MaybeFail<Fail> {
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
            .getProcessDefinitionKey(countryId = countryId, pmd = pmd, processName = request.context.processName)
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
                    owner = request.platformId,
                    token = request.context.token
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
                    relatedProcess = request.context.relatedProcess
                        ?.let { relatedProcess ->
                            ProcessInfo.RelatedProcess(
                                cpid = relatedProcess.cpid
                            )
                        }
                )
            }

        CamundaContext(propertyContainer)
            .apply {
                this.request = CamundaContext.Request(
                    id = request.context.id,
                    payload = request.payload
                )
            }

        val variables: Map<String, Any> = propertyContainer.toMap()
        processService.launchProcess(processDefinitionKey, request.operationId, variables)
        return MaybeFail.none()
    }

    override fun launch(event: ChronographEvent): MaybeFail<Fail> {
        val isLaunched = processService.isLaunchedProcess(operationId = event.operationId)
            .orReturnFail { return MaybeFail.fail(it) }
        if (isLaunched)
            return MaybeFail.fail(RequestErrors.Common.Repeated())

        val prevProcessContext: LatestProcessContext = processService.getProcessContext(cpid = event.cpid)
            .orReturnFail { return MaybeFail.fail(it) }
            ?: return MaybeFail.fail(Fail.Incident.Bpe(description = "The process context by cpid '${event.cpid}' does not found."))

        val countryId = prevProcessContext.country
        val pmd = prevProcessContext.pmd
        val processDefinitionKey = processService
            .getProcessDefinitionKey(countryId = countryId, pmd = pmd, processName = event.processName)
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
                    operationId = event.operationId,
                    timestamp = event.timestamp,
                    requestId = event.requestId,
                    platformId = event.platformId,
                    country = countryId,
                    language = prevProcessContext.language,
                    owner = event.platformId,
                    token = null
                )

                processInfo = ProcessInfo(
                    ocid = event.ocid,
                    cpid = event.cpid,
                    pmd = pmd,
                    operationType = rule.operationType,
                    stage = rule.stageTo,
                    prevStage = prevStage,
                    processDefinitionKey = processDefinitionKey,
                    phase = rule.phaseTo,
                    isAuction = prevProcessContext.isAuction,
                    mainProcurementCategory = prevProcessContext.mainProcurementCategory,
                    awardCriteria = prevProcessContext.awardCriteria,
                    relatedProcess = null
                )
            }

        CamundaContext(propertyContainer)
            .apply {
                this.request = CamundaContext.Request(id = null, payload = "")
            }

        val variables: Map<String, Any> = propertyContainer.toMap()
        processService.launchProcess(processDefinitionKey, event.operationId, variables)
        return MaybeFail.none()
    }

    private fun saveRequest(request: PlatformRequest): Result<RequestRecord, Fail.Incident> {
        val record = request.asRecord()
            .orForwardFail { fail -> return fail }
        requestRepository.save(record)
            .doOnError { return Result.failure(it) }
        return Result.success(record)
    }

    private fun PlatformRequest.asRecord(): Result<RequestRecord, Fail.Incident> {
        val serializedContext: String = transform.trySerialization(this.context)
            .orForwardFail { fail -> return fail }
        return RequestRecord(
            operationId = this.operationId,
            timestamp = nowDefaultUTC(),
            requestId = RequestId.generate(),
            platformId = this.platformId,
            context = serializedContext,
            payload = this.payload
        ).asSuccess()
    }
}
