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
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.qualification.QualificationCommands
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.RankQualificationsAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class RankQualificationsDelegate(
    logger: Logger,
    private val qualificationClient: QualificationClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, RankQualificationsAction.Result>(
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
    ): Result<Reply<RankQualificationsAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        val contextSubmissions = context.tryGetSubmissions()
            .orForwardFail { fail -> return fail }
            .getDetailsIfNotEmpty()
            .orForwardFail { fail -> return fail }

        val contextTender = context.tryGetTender()
            .orForwardFail { fail -> return fail }

        return qualificationClient.rankQualifications(
            id = commandId,
            params = RankQualificationsAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                submissions = contextSubmissions.map { submission ->
                    RankQualificationsAction.Params.Submission(
                        id = submission.id,
                        date = submission.date
                    )
                },
                tender = contextTender.let { tender ->
                    RankQualificationsAction.Params.Tender(
                        otherCriteria = tender.otherCriteria
                            .let { otherCriteria ->
                                RankQualificationsAction.Params.Tender.OtherCriteria(
                                    reductionCriteria = otherCriteria?.reductionCriteria,
                                    qualificationSystemMethods = otherCriteria?.qualificationSystemMethods
                                )
                            },
                        criteria = tender.criteria
                            .map { criterion ->
                                RankQualificationsAction.Params.Tender.Criteria(
                                    id = criterion.id,
                                    description = criterion.description,
                                    relatedItem = criterion.relatedItem,
                                    relatesTo = criterion.relatesTo,
                                    source = criterion.source,
                                    title = criterion.title,
                                    requirementGroups = criterion.requirementGroups
                                        .map {requirementGroup->
                                            RankQualificationsAction.Params.Tender.Criteria.RequirementGroup(
                                                id = requirementGroup.id,
                                                description = requirementGroup.description,
                                                requirements = requirementGroup.requirements
                                                    .map {requirement->
                                                        RankQualificationsAction.Params.Tender.Criteria.RequirementGroup.Requirement(
                                                            id = requirement.id,
                                                            description = requirement.description,
                                                            dataType = requirement.dataType,
                                                            title = requirement.title
                                                        )
                                                    }
                                            )
                                        }
                                )
                            }
                    )
                }

            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<RankQualificationsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.QUALIFICATION,
                    action = QualificationCommands.RankQualifications
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
        reqQualification: RankQualificationsAction.Result.Qualification
    ) = qualification.copy(statusDetails = reqQualification.statusDetails)
}
