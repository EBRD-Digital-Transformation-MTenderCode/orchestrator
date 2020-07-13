package com.procurement.orchestrator.infrastructure.bpms.delegate.qualification

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.QualificationClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getQualificationIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getRequirementResponseIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckDeclarationAction
import org.springframework.stereotype.Component

@Component
class QualificationCheckDeclarationDelegate(
    logger: Logger,
    private val qualificationClient: QualificationClient,
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
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<Unit>, Fail.Incident> {

        val processInfo = context.processInfo

        val qualification = context.getQualificationIfOnlyOne()
            .orForwardFail { fail -> return fail }

        val requirementResponse = qualification.getRequirementResponseIfOnlyOne()
            .orForwardFail { fail -> return fail }

        val criteria = context.tryGetTender()
            .orForwardFail { fail -> return fail }
            .criteria

        return qualificationClient.checkDeclaration(
            id = commandId,
            params = CheckDeclarationAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                qualificationId = qualification.id,
                requirementResponse = CheckDeclarationAction.Params.RequirementResponse(
                    id = requirementResponse.id,
                    value = requirementResponse.value!!,
                    relatedTendererId = requirementResponse.relatedTenderer!!.id,
                    requirementId = requirementResponse.requirement!!.id,
                    responder = requirementResponse.responder
                        ?.let { responder ->
                            CheckDeclarationAction.Params.RequirementResponse.Responder(
                                id = responder.id,
                                name = responder.name
                            )
                        }

                ),
                criteria = criteria.map { criterion ->
                    CheckDeclarationAction.Params.Criteria(
                        id = criterion.id,
                        requirementGroups = criterion.requirementGroups.map { requirementGroup ->
                            CheckDeclarationAction.Params.Criteria.RequirementGroup(
                                id = requirementGroup.id,
                                requirements = requirementGroup.requirements.map { requirement ->
                                    CheckDeclarationAction.Params.Criteria.RequirementGroup.Requirement(
                                        id = requirement.id,
                                        dataType = requirement.dataType
                                    )
                                }
                            )
                        }
                    )
                }
            )
        )
    }
}
