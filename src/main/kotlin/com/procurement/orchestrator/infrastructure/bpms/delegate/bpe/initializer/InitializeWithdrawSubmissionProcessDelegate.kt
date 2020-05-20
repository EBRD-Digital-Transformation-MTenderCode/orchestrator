package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer

import com.procurement.orchestrator.application.model.context.CamundaContext
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.repository.ProcessInitializerRepository
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.model.submission.Details
import com.procurement.orchestrator.domain.model.submission.Submission
import com.procurement.orchestrator.domain.model.submission.SubmissionId
import com.procurement.orchestrator.domain.model.submission.Submissions
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.springframework.stereotype.Component

@Component
class InitializeWithdrawSubmissionProcessDelegate(
    logger: Logger,
    transform: Transform,
    operationStepRepository: OperationStepRepository,
    processInitializerRepository: ProcessInitializerRepository
) : AbstractInitializeProcessDelegate(logger, transform, operationStepRepository, processInitializerRepository) {

    override fun updateGlobalContext(
        camundaContext: CamundaContext,
        globalContext: CamundaGlobalContext
    ): MaybeFail<Fail.Incident> {
        val submissionId = camundaContext.request.id
            ?.let { id ->
                SubmissionId.Permanent.tryCreateOrNull(id)
                    ?: return MaybeFail.fail(
                        Fail.Incident.Bpms.Context.DataFormatMismatch(
                            name = "id",
                            path = "#/request",
                            expectedFormat = SubmissionId.Permanent.pattern,
                            actualValue = id
                        )
                    )
            }
            ?: return MaybeFail.fail(
                Fail.Incident.Bpms.Context.Missing(name = "id", path = "#/request")
            )

        globalContext.submissions = Submissions(
            details = Details(
                Submission(
                    id = submissionId
                )
            )
        )

        return MaybeFail.none()
    }
}
