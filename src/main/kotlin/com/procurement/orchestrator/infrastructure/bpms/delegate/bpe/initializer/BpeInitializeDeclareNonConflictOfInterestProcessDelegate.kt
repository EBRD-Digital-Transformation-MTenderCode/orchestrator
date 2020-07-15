package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer

import com.procurement.orchestrator.application.model.context.CamundaContext
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetId
import com.procurement.orchestrator.application.repository.ProcessInitializerRepository
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.extension.date.tryParseLocalDateTime
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.fail.error.DataValidationErrors
import com.procurement.orchestrator.domain.fail.error.RequestErrors
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.award.Award
import com.procurement.orchestrator.domain.model.award.AwardId
import com.procurement.orchestrator.domain.model.award.Awards
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
import com.procurement.orchestrator.domain.model.person.PersonId
import com.procurement.orchestrator.domain.model.requirement.RequirementReference
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponse
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponses
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.springframework.stereotype.Component

@Component
class BpeInitializeDeclareNonConflictOfInterestProcessDelegate(
    val logger: Logger,
    private val transform: Transform,
    operationStepRepository: OperationStepRepository,
    processInitializerRepository: ProcessInitializerRepository
) : AbstractInitializeProcessDelegate(logger, transform, operationStepRepository, processInitializerRepository) {

    override fun updateGlobalContext(
        camundaContext: CamundaContext,
        globalContext: CamundaGlobalContext
    ): MaybeFail<Fail> {
        val awardId = camundaContext.request.tryGetId()
            .orReturnFail { return MaybeFail.fail(it) }
            .let { id ->
                AwardId.tryCreateOrNull(id)
                    ?: return MaybeFail.fail(
                        RequestErrors.Common.DataFormatMismatch(
                            name = "awardId",
                            expectedFormat = AwardId.pattern,
                            actualValue = id
                        )
                    )
            }

        val payload: DeclareNonConflictOfInterest.Request.Payload =
            parsePayload(camundaContext.request.payload, DeclareNonConflictOfInterest.Request.Payload::class.java)
            .orReturnFail { return MaybeFail.fail(it) }

        globalContext.awards = buildAwards(awardId, payload)
            .orReturnFail { return MaybeFail.fail(it) }

        return MaybeFail.none()
    }

    private fun buildAwards(
        awardId: AwardId,
        payload: DeclareNonConflictOfInterest.Request.Payload
    ): Result<Awards, Fail> = Awards(
        Award(
            id = awardId,
            requirementResponses = payload.requirementResponse
                .let { requirementResponse ->
                    RequirementResponse(
                        id = requirementResponse.id,
                        value = requirementResponse.value,
                        relatedTenderer = OrganizationReference(
                            id = requirementResponse.relatedTenderer.id
                        ),
                        responder = requirementResponse.responder
                            .let { responder ->
                                Person(
                                    id = PersonId.generate(
                                        scheme = responder.identifier.scheme,
                                        id = responder.identifier.id
                                    ),
                                    identifier = responder.identifier
                                        .let { identifier ->
                                            Identifier(
                                                scheme = identifier.scheme,
                                                id = identifier.id,
                                                uri = identifier.uri
                                            )
                                        },
                                    name = responder.name,
                                    title = responder.title,
                                    businessFunctions = responder.businessFunctions
                                        .map { businessFunction ->
                                            BusinessFunction(
                                                id = businessFunction.id,
                                                type = BusinessFunctionType.orNull(businessFunction.type)
                                                    ?: return failure(
                                                        DataValidationErrors.UnknownValue(
                                                            name = "awards[id:$awardId].businessFunctions[id:${businessFunction.id}].type",
                                                            expectedValues = BusinessFunctionType.allowedElements.keysAsStrings(),
                                                            actualValue = businessFunction.type
                                                        )
                                                    ),
                                                jobTitle = businessFunction.jobTitle,
                                                period = businessFunction.period
                                                    .let { period ->
                                                        Period(startDate = period.startDate.tryParseLocalDateTime()
                                                            .orReturnFail { fail ->
                                                                return failure(
                                                                    DataValidationErrors.DataFormatMismatch(
                                                                        name = "awards[id:$awardId].businessFunctions[id:${businessFunction.id}].period.startDate",
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
                                                                ?: return failure(
                                                                    DataValidationErrors.UnknownValue(
                                                                        name = "awards[id:$awardId].businessFunctions[id:${businessFunction.id}].documents[id:${document.id}].documentType",
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
    ).asSuccess()
}
