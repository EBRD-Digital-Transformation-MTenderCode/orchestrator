package com.procurement.orchestrator.application.service.response

import com.procurement.orchestrator.application.model.RequestId
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.container.DefaultPropertyContainer
import com.procurement.orchestrator.application.model.context.members.Awards
import com.procurement.orchestrator.application.model.context.members.ProcessInfo
import com.procurement.orchestrator.application.model.context.members.RequestInfo
import com.procurement.orchestrator.application.model.process.LatestProcessContext
import com.procurement.orchestrator.application.repository.RuleRepository
import com.procurement.orchestrator.application.service.ProcessService
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.extension.date.nowDefaultUTC
import com.procurement.orchestrator.domain.extension.date.tryParseLocalDateTime
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.fail.error.BpeErrors
import com.procurement.orchestrator.domain.fail.error.DataValidationErrors
import com.procurement.orchestrator.domain.fail.error.RequestErrors
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.award.Award
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.DocumentType
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.identifier.Identifier
import com.procurement.orchestrator.domain.model.organization.OrganizationReference
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunction
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctionType
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctions
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.person.Person
import com.procurement.orchestrator.domain.model.requirement.RequirementReference
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponse
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponses
import com.procurement.orchestrator.infrastructure.bpms.repository.RequestRecord
import com.procurement.orchestrator.infrastructure.bpms.repository.RequestRepository

interface RequirementResponseService {
    fun declareNoConflictOfInterest(request: RequirementResponseDataIn.Request): MaybeFail<Fail>
}

class RequirementResponseServiceImpl(
    private val transform: Transform,
    private val processService: ProcessService,
    private val requestRepository: RequestRepository,
    private val ruleRepository: RuleRepository
) : RequirementResponseService {

    companion object {
        private const val DECLARE_NON_CONFLICT_OF_INTEREST = "declareNonConflictOfInterest"
    }

    override fun declareNoConflictOfInterest(request: RequirementResponseDataIn.Request): MaybeFail<Fail> {
        val savedRequest: RequestRecord = saveRequest(request)
            .orReturnFail { return MaybeFail.fail(it) }

        val isLaunched = processService.isLaunchedProcess(operationId = request.operationId)
            .orReturnFail { return MaybeFail.fail(it) }
        if (isLaunched)
            return MaybeFail.fail(RequestErrors.Common.Repeated())

        val payload = deserializationPayload<RequirementResponseDataIn.Payload>(request.payload)
            .orReturnFail { return MaybeFail.fail(it) }

        val prevProcessContext: LatestProcessContext = processService.getProcessContext(cpid = request.context.cpid)
            .orReturnFail { return MaybeFail.fail(it) }
            ?: return MaybeFail.fail(Fail.Incident.Bpe(description = "The process context by cpid '${request.context.cpid}' does not found."))

        val countryId = prevProcessContext.country
        val pmd = prevProcessContext.pmd

        val processDefinitionKey = processService
            .getProcessDefinitionKey(countryId = countryId, pmd = pmd, processName = DECLARE_NON_CONFLICT_OF_INTEREST)
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
                BpeErrors.Process(
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
                    language = prevProcessContext.language
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
                    awardCriteria = prevProcessContext.awardCriteria
                )

                awards = Awards(
                    Award(
                        id = request.context.awardId,
                        owner = request.platformId,
                        token = request.context.token,
                        requirementResponses = payload.requirementResponse
                            .let { requirementResponse ->
                                RequirementResponse(
                                    id = RequirementResponseId.Temporal.create(requirementResponse.id),
                                    value = requirementResponse.value,
                                    relatedTenderer = OrganizationReference(
                                        id = requirementResponse.relatedTenderer.id
                                    ),
                                    responder = requirementResponse.responder
                                        .let { responder ->
                                            Person(
                                                title = responder.title,
                                                name = responder.name,
                                                identifier = responder.identifier
                                                    .let { identifier ->
                                                        Identifier(
                                                            scheme = identifier.scheme,
                                                            id = identifier.id,
                                                            uri = identifier.uri
                                                        )
                                                    },
                                                businessFunctions = responder.businessFunctions
                                                    .map { businessFunction ->
                                                        BusinessFunction(
                                                            id = businessFunction.id,
                                                            type = BusinessFunctionType.orNull(businessFunction.type)
                                                                ?: return MaybeFail.fail(
                                                                    DataValidationErrors.UnknownValue(
                                                                        name = "awards[id:${request.context.awardId}].businessFunctions[id:${businessFunction.id}].type",
                                                                        expectedValues = BusinessFunctionType.allowedElements.keysAsStrings(),
                                                                        actualValue = businessFunction.type
                                                                    )
                                                                ),
                                                            jobTitle = businessFunction.jobTitle,
                                                            period = businessFunction.period
                                                                .let { period ->
                                                                    Period(startDate = period.startDate.tryParseLocalDateTime()
                                                                        .orReturnFail { fail ->
                                                                            return MaybeFail.fail(
                                                                                DataValidationErrors.DataFormatMismatch(
                                                                                    name = "awards[id:${request.context.awardId}].businessFunctions[id:${businessFunction.id}].period.startDate",
                                                                                    expectedFormat = fail,
                                                                                    actualValue = period.startDate
                                                                                )
                                                                            )
                                                                        }
                                                                    )
                                                                },
                                                            documents = businessFunction.documents
                                                                .map { document ->
                                                                    Document(
                                                                        id = DocumentId.create(document.id),
                                                                        documentType = DocumentType.orNull(document.documentType)
                                                                            ?: return MaybeFail.fail(
                                                                                DataValidationErrors.UnknownValue(
                                                                                    name = "awards[id:${request.context.awardId}].businessFunctions[id:${businessFunction.id}].documents[id:${document.id}].documentType",
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
                                                    .let { BusinessFunctions(it) }
                                            )
                                        },
                                    requirement = RequirementReference(
                                        id = requirementResponse.requirement.id
                                    )
                                )
                            }
                            .let { RequirementResponses(it) }
                    )
                )
            }

        val variables = propertyContainer.toMap()
        processService.launchProcess(processDefinitionKey, request.operationId, variables)
        return MaybeFail.none()
    }

    private inline fun <reified T> deserializationPayload(payload: String): Result<T, RequestErrors.Payload> =
        transform.tryDeserialization(value = payload, target = T::class.java)
            .mapError { fail ->
                RequestErrors.Payload.Parsing(
                    description = fail.description,
                    payload = payload,
                    exception = fail.exception
                )
            }

    private fun saveRequest(request: RequirementResponseDataIn.Request): Result<RequestRecord, Fail.Incident> {
        val record = request.asRecord()
            .orForwardFail { fail -> return fail }
        requestRepository.save(record)
            .doOnError { return Result.failure(it) }
        return Result.success(record)
    }

    private fun RequirementResponseDataIn.Request.asRecord(): Result<RequestRecord, Fail.Incident> {
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
