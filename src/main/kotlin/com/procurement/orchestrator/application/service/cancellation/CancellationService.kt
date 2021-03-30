package com.procurement.orchestrator.application.service.cancellation

import com.procurement.orchestrator.application.model.RequestId
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.container.DefaultPropertyContainer
import com.procurement.orchestrator.application.model.context.members.ProcessInfo
import com.procurement.orchestrator.application.model.context.members.RequestInfo
import com.procurement.orchestrator.application.model.process.LatestProcessContext
import com.procurement.orchestrator.application.repository.RuleRepository
import com.procurement.orchestrator.application.service.ProcessService
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.extension.date.nowDefaultUTC
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.fail.error.BpeErrors
import com.procurement.orchestrator.domain.fail.error.DataValidationErrors
import com.procurement.orchestrator.domain.fail.error.RequestErrors
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.amendment.Amendment
import com.procurement.orchestrator.domain.model.amendment.Amendments
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.DocumentType
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.lot.Lot
import com.procurement.orchestrator.domain.model.lot.Lots
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.infrastructure.bpms.repository.RequestRecord
import com.procurement.orchestrator.infrastructure.bpms.repository.RequestRepository

interface CancellationService {
    fun cancelTender(request: CancellationTender.Request): MaybeFail<Fail>
    fun cancelLot(request: CancellationLot.Request): MaybeFail<Fail>
    fun cancelAmendment(request: CancellationAmendment.Request): MaybeFail<Fail>
}

class CancellationServiceImpl(
    private val transform: Transform,
    private val processService: ProcessService,
    private val requestRepository: RequestRepository,
    private val ruleRepository: RuleRepository
) : CancellationService {

    companion object {
        private const val CANCEL_TENDER_PROCESS = "cancelTender"
        private const val CANCEL_LOT_PROCESS = "cancelLot"
        private const val CANCEL_AMENDMENT_PROCESS = "tenderOrLotAmendmentCancellation"
    }

    override fun cancelTender(request: CancellationTender.Request): MaybeFail<Fail> {
        val savedRequest: RequestRecord = saveRequest(request)
            .orReturnFail { return MaybeFail.fail(it) }

        val isLaunched = processService.isLaunchedProcess(operationId = request.operationId)
            .orReturnFail { return MaybeFail.fail(it) }
        if (isLaunched)
            return MaybeFail.fail(RequestErrors.Common.Repeated())

        val payload = parsePayload<CancellationTender.Payload>(request.payload)
            .orReturnFail { return MaybeFail.fail(it) }

        val prevProcessContext: LatestProcessContext = processService.getProcessContext(cpid = request.context.cpid)
            .orReturnFail { return MaybeFail.fail(it) }
            ?: return MaybeFail.fail(Fail.Incident.Bpe(description = "The process context by cpid '${request.context.cpid}' does not found."))

        val countryId = prevProcessContext.country
        val pmd = prevProcessContext.pmd

        val processDefinitionKey = processService
            .getProcessDefinitionKey(countryId = countryId, pmd = pmd, processName = CANCEL_TENDER_PROCESS)
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
                    owner = request.platformId
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
                    relatedProcess = null,
                    entityId = null,
                    documentInitiator = null,
                    additionalProcess = null
                )

                tender = Tender(
                    amendments = payload.amendments
                        .map { amendment ->
                            Amendment(
                                id = amendment.id,
                                rationale = amendment.rationale,
                                description = amendment.description,
                                documents = amendment.documents
                                    .map { document ->
                                        Document(
                                            id = DocumentId.create(document.id),
                                            documentType = DocumentType.orNull(document.documentType)
                                                ?: return MaybeFail.fail(
                                                    DataValidationErrors.UnknownValue(
                                                        name = "amendment.documents[id:${document.id}].documentType",
                                                        expectedValues = DocumentType.allowedElements.keysAsStrings(),
                                                        actualValue = document.documentType
                                                    )
                                                ),
                                            title = document.title,
                                            description = document.description
                                        )
                                    }
                                    .let { Documents(it) }
                            )
                        }
                        .let { Amendments(it) }
                )
            }

        val variables = propertyContainer.toMap()
        processService.launchProcess(processDefinitionKey, request.operationId, variables)
        return MaybeFail.none()
    }

    override fun cancelLot(request: CancellationLot.Request): MaybeFail<Fail> {
        val savedRequest: RequestRecord = saveRequest(request)
            .orReturnFail { return MaybeFail.fail(it) }

        val isLaunched = processService.isLaunchedProcess(operationId = request.operationId)
            .orReturnFail { return MaybeFail.fail(it) }
        if (isLaunched)
            return MaybeFail.fail(RequestErrors.Common.Repeated())

        val payload = parsePayload<CancellationLot.Payload>(request.payload)
            .orReturnFail { return MaybeFail.fail(it) }

        val prevProcessContext: LatestProcessContext = processService.getProcessContext(cpid = request.context.cpid)
            .orReturnFail { return MaybeFail.fail(it) }
            ?: return MaybeFail.fail(Fail.Incident.Bpe(description = "The process context by cpid '${request.context.cpid}' does not found."))

        val countryId = prevProcessContext.country
        val pmd = prevProcessContext.pmd

        val processDefinitionKey = processService
            .getProcessDefinitionKey(countryId = countryId, pmd = pmd, processName = CANCEL_LOT_PROCESS)
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
                    owner = request.platformId
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
                    relatedProcess = null,
                    entityId = null,
                    documentInitiator = null,
                    additionalProcess = null
                )

                tender = Tender(
                    lots = Lots(
                        Lot(id = request.context.lotId)
                    ),
                    amendments = payload.amendments
                        .map { amendment ->
                            Amendment(
                                id = amendment.id,
                                rationale = amendment.rationale,
                                description = amendment.description,
                                documents = amendment.documents
                                    .map { document ->
                                        Document(
                                            id = DocumentId.create(document.id),
                                            documentType = DocumentType.orNull(document.documentType)
                                                ?: return MaybeFail.fail(
                                                    DataValidationErrors.UnknownValue(
                                                        name = "amendment.documents[id:${document.id}].documentType",
                                                        expectedValues = DocumentType.allowedElements.keysAsStrings(),
                                                        actualValue = document.documentType
                                                    )
                                                ),
                                            title = document.title,
                                            description = document.description
                                        )
                                    }
                                    .let { Documents(it) }
                            )
                        }
                        .let { Amendments(it) }
                )
            }

        val variables = propertyContainer.toMap()
        processService.launchProcess(processDefinitionKey, request.operationId, variables)
        return MaybeFail.none()
    }

    override fun cancelAmendment(request: CancellationAmendment.Request): MaybeFail<Fail> {
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
            .getProcessDefinitionKey(countryId = countryId, pmd = pmd, processName = CANCEL_AMENDMENT_PROCESS)
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
                    relatedProcess = null,
                    entityId = null,
                    documentInitiator = null,
                    additionalProcess = null
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

    private inline fun <reified T> parsePayload(payload: String): Result<T, RequestErrors.Payload> =
        transform.tryDeserialization(value = payload, target = T::class.java)
            .mapError { fail ->
                RequestErrors.Payload.Parsing(
                    description = fail.description,
                    payload = payload,
                    exception = fail.exception
                )
            }

    private fun saveRequest(request: CancellationTender.Request): Result<RequestRecord, Fail.Incident> {
        val record = request.asRecord()
            .orForwardFail { fail -> return fail }
        requestRepository.save(record)
            .doOnError { return failure(it) }
        return success(record)
    }

    private fun CancellationTender.Request.asRecord(): Result<RequestRecord, Fail.Incident> {
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

    private fun saveRequest(request: CancellationLot.Request): Result<RequestRecord, Fail.Incident> {
        val record = request.asRecord()
            .orForwardFail { fail -> return fail }
        requestRepository.save(record)
            .doOnError { return failure(it) }
        return success(record)
    }

    private fun CancellationLot.Request.asRecord(): Result<RequestRecord, Fail.Incident> {
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

    private fun saveRequest(request: CancellationAmendment.Request): Result<RequestRecord, Fail.Incident> {
        val record = request.asRecord()
            .orForwardFail { fail -> return fail }
        requestRepository.save(record)
            .doOnError { return failure(it) }
        return success(record)
    }

    private fun CancellationAmendment.Request.asRecord(): Result<RequestRecord, Fail.Incident> {
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
