package com.procurement.orchestrator.infrastructure.bpms.delegate.qualification

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.QualificationClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getDetailsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getQualificationsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.tryGetSubmissions
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.qualification.Qualification
import com.procurement.orchestrator.domain.model.qualification.Qualifications
import com.procurement.orchestrator.domain.model.submission.SubmissionId
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.qualification.QualificationCommands
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.GetNextsForQualificationAction
import org.springframework.stereotype.Component

@Component
class QualificationGetNextsForQualificationDelegate(
    logger: Logger,
    private val qualificationClient: QualificationClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, GetNextsForQualificationAction.Result>(
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
    ): Result<Reply<GetNextsForQualificationAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val contextQualifications = context.getQualificationsIfNotEmpty()
            .orForwardFail { fail -> return fail }

        val contextSubmissions = context.tryGetSubmissions()
            .orForwardFail { fail -> return fail }
            .getDetailsIfNotEmpty()
            .orForwardFail { fail -> return fail }

        val contextTender = context.tryGetTender()
            .orForwardFail { fail -> return fail }

        return qualificationClient.getNextsForQualification(
            id = commandId,
            params = GetNextsForQualificationAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                qualifications = contextQualifications.map { qualification ->
                    GetNextsForQualificationAction.Params.Qualification(
                        id = qualification.id,
                        relatedSubmission = qualification.relatedSubmission as SubmissionId.Permanent,
                        date = qualification.date,
                        scoring = qualification.scoring
                    )
                },
                submissions = contextSubmissions.map { submission ->
                    GetNextsForQualificationAction.Params.Submission(
                        id = submission.id as SubmissionId.Permanent,
                        date = submission.date
                    )
                },
                reductionCriteria = contextTender.otherCriteria?.reductionCriteria,
                qualificationSystemMethods = contextTender.otherCriteria?.qualificationSystemMethods
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<GetNextsForQualificationAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = "eQualification",
                    action = QualificationCommands.GetNextsForQualification
                )
            )

        val contextQualifications = context.getQualificationsIfNotEmpty()
            .orReturnFail { fail -> return MaybeFail.fail(fail) }

        val receivedQualificationsByIds = data.associateBy { it.id }

        val updatedQualifications = contextQualifications.map { qualification ->
            receivedQualificationsByIds[qualification.id]
                ?.let { updateQualification(qualification = qualification, reqQualification = it) }
                ?: qualification
        }

        context.qualifications = Qualifications(values = updatedQualifications)
        return MaybeFail.none()
    }

    private fun updateQualification(
        qualification: Qualification,
        reqQualification: GetNextsForQualificationAction.Result.Qualification
    ) = qualification.copy(statusDetails = reqQualification.statusDetails)
}
