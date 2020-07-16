package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer

import com.procurement.orchestrator.application.model.context.CamundaContext
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetId
import com.procurement.orchestrator.application.repository.ProcessInitializerRepository
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
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
import com.procurement.orchestrator.domain.model.qualification.Qualification
import com.procurement.orchestrator.domain.model.qualification.QualificationId
import com.procurement.orchestrator.domain.model.qualification.QualificationStatusDetails
import com.procurement.orchestrator.domain.model.qualification.Qualifications
import com.procurement.orchestrator.domain.util.extension.mapToResult
import com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer.DoQualification.Request
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.springframework.stereotype.Component

@Component
class BpeInitializeQualificationProcessDelegate(
    val logger: Logger,
    transform: Transform,
    operationStepRepository: OperationStepRepository,
    processInitializerRepository: ProcessInitializerRepository
) : AbstractInitializeProcessDelegate(logger, transform, operationStepRepository, processInitializerRepository) {

    override fun updateGlobalContext(
        camundaContext: CamundaContext, globalContext: CamundaGlobalContext
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

        val payload: Request.Payload = parsePayload(camundaContext.request.payload, Request.Payload::class.java)
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

        val qualification = payload.qualification

        val documents = qualification.documents
            .mapToResult { receivedDocument -> createDocument(qualificationId, receivedDocument) }
            .orForwardFail { fail -> return fail }

        val parsedStatusDetails = qualification.statusDetails
            .let { statusDetails ->
                QualificationStatusDetails.orNull(statusDetails)
                    ?: return failure(
                        DataValidationErrors.UnknownValue(
                            name = "qualification[id:$qualificationId].statusDetails",
                            expectedValues = QualificationStatusDetails.allowedElements.keysAsStrings(),
                            actualValue = statusDetails
                        )
                    )
            }

        return Qualification(
            id = qualificationId,
            internalId = qualification.internalId,
            statusDetails = parsedStatusDetails,
            description = qualification.description,
            documents = Documents(documents)
        ).asSuccess()
    }

    private fun createDocument(
        qualificationId: QualificationId,
        document: Request.Payload.Document
    ): Result<Document, DataValidationErrors.UnknownValue> {

        val documentType = DocumentType.orNull(document.documentType)
            ?: return failure(
                DataValidationErrors.UnknownValue(
                    name = "qualification[id:$qualificationId].documents[id:${document.id}].documentType",
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
