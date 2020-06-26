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
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.DocumentType
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.identifier.Identifier
import com.procurement.orchestrator.domain.model.organization.OrganizationReference
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunction
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctionId
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctionType
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctions
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.person.Person
import com.procurement.orchestrator.domain.model.person.PersonId
import com.procurement.orchestrator.domain.model.qualification.Qualification
import com.procurement.orchestrator.domain.model.qualification.QualificationId
import com.procurement.orchestrator.domain.model.qualification.Qualifications
import com.procurement.orchestrator.domain.model.requirement.RequirementReference
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponse
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponses
import com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer.QualificationDeclareNonConflictOfInterest.Request
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.springframework.stereotype.Component

@Component
class BpeInitializeQualificationDeclareNonConflictOfInterestProcessDelegate(
    val logger: Logger,
    private val transform: Transform,
    operationStepRepository: OperationStepRepository,
    processInitializerRepository: ProcessInitializerRepository
) : AbstractInitializeProcessDelegate(logger, transform, operationStepRepository, processInitializerRepository) {

    override fun updateGlobalContext(
        camundaContext: CamundaContext,
        globalContext: CamundaGlobalContext
    ): MaybeFail<Fail> {
        val qualificationId = camundaContext.request.tryGetId()
            .orReturnFail { return MaybeFail.fail(it) }
            .let { id ->
                QualificationId.tryCreateOrNull(id)
                    ?: return MaybeFail.fail(
                        RequestErrors.Common.DataFormatMismatch(
                            name = "qualificationId",
                            expectedFormat = QualificationId.pattern,
                            actualValue = id
                        )
                    )
            }

        val payload: Request.Payload = transform.tryDeserialization(
            value = camundaContext.request.payload,
            target = Request.Payload::class.java
        )
            .orReturnFail { return MaybeFail.fail(it) }

        val createdQualification = createQualification(qualificationId, payload)
            .orReturnFail { return MaybeFail.fail(it) }

        globalContext.qualifications = Qualifications(createdQualification)

        return MaybeFail.none()
    }

    private fun createQualification(
        qualificationId: QualificationId,
        payload: Request.Payload
    ): Result<Qualification, DataValidationErrors> {

        val requirementResponse = createRequirementResponse(qualificationId, payload.requirementResponse)
            .orForwardFail { fail -> return fail }

        return Qualification(
            id = qualificationId,
            requirementResponses = RequirementResponses(requirementResponse)
        ).asSuccess()
    }

    private fun createRequirementResponse(
        qualificationId: QualificationId,
        requirementResponse: Request.Payload.RequirementResponse
    ): Result<RequirementResponse, DataValidationErrors> {
        val relatedTenderer = createRelatedTenderer(requirementResponse.relatedTenderer)

        val responder = createResponder(qualificationId, requirementResponse.responder)
            .orForwardFail { fail -> return fail }

        val requirement = createRequirementReference(requirementResponse.requirement)

        return RequirementResponse(
            id = requirementResponse.id,
            value = requirementResponse.value,
            relatedTenderer = relatedTenderer,
            responder = responder,
            requirement = requirement
        ).asSuccess()
    }

    private fun createRelatedTenderer(
        relatedTenderer: Request.Payload.RequirementResponse.RelatedTenderer
    ): OrganizationReference = OrganizationReference(id = relatedTenderer.id)

    private fun createRequirementReference(
        requirement: Request.Payload.RequirementResponse.Requirement
    ): RequirementReference = RequirementReference(id = requirement.id)

    private fun createResponder(
        qualificationId: QualificationId,
        responder: Request.Payload.RequirementResponse.Responder
    ): Result<Person, DataValidationErrors> {

        val generatedId = PersonId.generate(
            scheme = responder.identifier.scheme,
            id = responder.identifier.id
        )

        val identifier = createIdentifier(responder.identifier)

        val businessFunctions = responder.businessFunctions
            .map {
                createBusinessFunction(qualificationId, it)
                    .orForwardFail { fail -> return fail }
            }

        return Person(
            id = generatedId,
            identifier = identifier,
            name = responder.name,
            title = responder.title,
            businessFunctions = BusinessFunctions(businessFunctions)
        ).asSuccess()
    }

    private fun createIdentifier(
        identifier: Request.Payload.RequirementResponse.Responder.Identifier
    ): Identifier = Identifier(
        scheme = identifier.scheme,
        id = identifier.id,
        uri = identifier.uri
    )

    private fun createBusinessFunction(
        qualificationId: QualificationId,
        businessFunction: Request.Payload.RequirementResponse.Responder.BusinessFunction
    ): Result<BusinessFunction, DataValidationErrors> {

        val documents = businessFunction.documents
            .map { document ->
                createDocument(qualificationId, businessFunction.id, document)
                    .orForwardFail { fail -> return fail }
            }

        val businessFunctionType = BusinessFunctionType.orNull(businessFunction.type)
            ?: return failure(
                DataValidationErrors.UnknownValue(
                    name = "qualification[id:$qualificationId].businessFunctions[id:${businessFunction.id}].type",
                    expectedValues = BusinessFunctionType.allowedElements.keysAsStrings(),
                    actualValue = businessFunction.type
                )
            )

        val period = businessFunction.period
            .let { period ->
                Period(startDate = period.startDate.tryParseLocalDateTime()
                    .orReturnFail { fail ->
                        return failure(
                            DataValidationErrors.DataFormatMismatch(
                                name = "qualification[id:$qualificationId].businessFunctions[id:${businessFunction.id}].period.startDate",
                                expectedFormat = fail,
                                actualValue = period.startDate
                            )
                        )
                    }
                )
            }

        return BusinessFunction(
            id = businessFunction.id,
            type = businessFunctionType,
            jobTitle = businessFunction.jobTitle,
            period = period,
            documents = Documents(documents)
        ).asSuccess()
    }

    private fun createDocument(
        qualificationId: QualificationId,
        businessFunctionId: BusinessFunctionId,
        document: Request.Payload.RequirementResponse.Responder.BusinessFunction.Document
    ): Result<Document, DataValidationErrors.UnknownValue> {

        val documentType = DocumentType.orNull(document.documentType)
            ?: return failure(
                DataValidationErrors.UnknownValue(
                    name = "qualification[id:$qualificationId].businessFunctions[id:${businessFunctionId}].documents[id:${document.id}].documentType",
                    expectedValues = DocumentType.allowedElements.keysAsStrings(),
                    actualValue = document.documentType
                )
            )

        return Document(
            id = DocumentId.create(document.id),
            documentType = documentType,
            title = document.title,
            description = document.description
        ).asSuccess()
    }
}
