package com.procurement.orchestrator.infrastructure.bpms.delegate.dossier

import com.procurement.orchestrator.application.client.DossierClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.ValidateRequirementResponseAction
import org.springframework.stereotype.Component

@Component
class DossierValidateRequirementResponseDelegate(
    logger: Logger,
    private val dossierClient: DossierClient,
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

        val processInfo = context.processInfo
        val cpid: Cpid = processInfo.cpid
        val ocid: Ocid = processInfo.ocid

        val awards = context.awards
            .takeIf { it.isNotEmpty() }
            ?: return failure(Fail.Incident.Bpe(description = "The global context does not contain a 'Awards' object."))
        if (awards.size != 1)
            return failure(
                Fail.Incident.Bpmn.Context.UnConsistency(
                    name = "awards",
                    description = "It was expected that the attribute 'awards' would have only one value. In fact, the attribute has ${awards.size} meanings."
                )
            )

        val requirementResponses = awards[0].requirementResponses
            .takeIf { it.isNotEmpty() }
            ?: return failure(Fail.Incident.Bpe(description = "The global context does not contain a 'requirementResponses' object in 'Award'."))
        if (requirementResponses.size != 1)
            return failure(
                Fail.Incident.Bpmn.Context.UnConsistency(
                    name = "awards.requirementResponses",
                    description = "It was expected that the attribute 'awards.requirementResponses' would have only one value. In fact, the attribute has ${requirementResponses.size} meanings."
                )
            )

        val requirementResponse = requirementResponses[0]

        return dossierClient.validateRequirementResponse(
            params = ValidateRequirementResponseAction.Params(
                cpid = cpid,
                ocid = ocid,
                requirementResponse = ValidateRequirementResponseAction.Params.RequirementResponse(
                    id = requirementResponse.id as RequirementResponseId.Temporal,
                    value = requirementResponse.value,
                    requirement = requirementResponse.requirement
                        ?.let { requirement ->
                            ValidateRequirementResponseAction.Params.RequirementResponse.Requirement(
                                id = requirement.id
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
