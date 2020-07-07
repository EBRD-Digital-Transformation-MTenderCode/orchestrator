package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer

import com.procurement.orchestrator.application.model.context.CamundaContext
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetId
import com.procurement.orchestrator.application.repository.ProcessInitializerRepository
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.fail.error.RequestErrors
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.model.qualification.Qualification
import com.procurement.orchestrator.domain.model.qualification.QualificationId
import com.procurement.orchestrator.domain.model.qualification.Qualifications
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.springframework.stereotype.Component

@Component
class BpeInitializeQualificationConsiderationProcessDelegate(
    val logger: Logger,
    transform: Transform,
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

        globalContext.qualifications = Qualifications(Qualification(id = qualificationId))

        return MaybeFail.none()
    }
}
