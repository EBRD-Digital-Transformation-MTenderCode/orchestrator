package com.procurement.orchestrator.infrastructure.bpms.delegate.revision

import com.procurement.orchestrator.application.client.RevisionClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.amendment.AmendmentId
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.revision.action.DataValidationAction
import org.springframework.stereotype.Component

@Component
class RevisionDataValidationDelegate(
    logger: Logger,
    private val client: RevisionClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, Unit>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<Unit>, Fail.Incident> {

        val tender = context.tender
            ?: Fail.Incident.Bpe(description = "The global context does not contain a 'Tender' object.")
                .throwIncident()

        if (tender.amendments.size != 1)
            return failure(
                Fail.Incident.Bpmn.Context.UnConsistency(
                    name = "tender.amendments",
                    description = "It was expected that the attribute 'tender.amendments' would have only one value. In fact, the attribute has ${tender.amendments.size} meanings"
                )
            )

        val amendment = tender.amendments[0]
        val processInfo = context.processInfo

        return client.dataValidation(
            params = DataValidationAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                operationType = processInfo.operationType,
                amendment = DataValidationAction.Params.Amendment(
                    id = amendment.id as AmendmentId.Temporal,
                    rationale = amendment.rationale,
                    description = amendment.description,
                    documents = amendment.documents
                        .map { document ->
                            DataValidationAction.Params.Amendment.Document(
                                id = document.id,
                                documentType = document.documentType,
                                title = document.title,
                                description = document.description
                            )
                        }
                )
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        data: Unit
    ): MaybeFail<Fail.Incident.Bpmn> = MaybeFail.none()
}
