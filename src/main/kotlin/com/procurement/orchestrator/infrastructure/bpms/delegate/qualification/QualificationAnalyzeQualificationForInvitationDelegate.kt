package com.procurement.orchestrator.infrastructure.bpms.delegate.qualification

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.QualificationClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.qualification.Qualification
import com.procurement.orchestrator.domain.model.qualification.Qualifications
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.qualification.QualificationCommands
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.AnalyzeQualificationForInvitationAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class QualificationAnalyzeQualificationForInvitationDelegate(
    logger: Logger,
    private val client: QualificationClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, AnalyzeQualificationForInvitationAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {
    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        Result.success(Unit)

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<AnalyzeQualificationForInvitationAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo

        return client.analyzeQualificationForInvitation(
            id = commandId,
            params = AnalyzeQualificationForInvitationAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                country = requestInfo.country,
                pmd = processInfo.pmd
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<AnalyzeQualificationForInvitationAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.QUALIFICATION,
                    action = QualificationCommands.RankQualifications
                )
            )

        val contextQualifications = context.qualifications
        val contextQualificationsByIds = contextQualifications
            .associateBy { it.id }

        val updatedQualifications = data.qualifications
            .map { reqQualification ->
                contextQualificationsByIds[reqQualification.id]
                    ?.let { updateQualification(qualification = it, reqQualification = reqQualification) }
                    ?: buildQualification(reqQualification = reqQualification)
            }

        context.qualifications = Qualifications(values = updatedQualifications)

        return MaybeFail.none()
    }

    private fun updateQualification(
        qualification: Qualification,
        reqQualification: AnalyzeQualificationForInvitationAction.Result.Qualification
    ) = qualification.copy(
        statusDetails = reqQualification.statusDetails,
        relatedSubmission = reqQualification.relatedSubmission,
        status = reqQualification.status
    )

    private fun buildQualification(reqQualification: AnalyzeQualificationForInvitationAction.Result.Qualification) =
        Qualification(
            id = reqQualification.id,
            status = reqQualification.status,
            relatedSubmission = reqQualification.relatedSubmission,
            statusDetails = reqQualification.statusDetails
        )
}
