package com.procurement.orchestrator.infrastructure.bpms.delegate.qualification

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.QualificationClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getDetailsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.tryGetSubmissions
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.model.context.members.Outcomes
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
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CreateQualificationAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class QualificationCreateQualificationsDelegate(
    logger: Logger,
    private val qualificationClient: QualificationClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, CreateQualificationAction.Result>(
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
    ): Result<Reply<CreateQualificationAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo

        val contextSubmissions = context.tryGetSubmissions()
            .orForwardFail { fail -> return fail }
            .getDetailsIfNotEmpty()
            .orForwardFail { fail -> return fail }

        val contextTender = context.tryGetTender()
            .orForwardFail { fail -> return fail }

        return qualificationClient.createQualification(
            id = commandId,
            params = CreateQualificationAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                date = requestInfo.timestamp,
                owner = requestInfo.owner,
                submissions = contextSubmissions.map { submission ->
                    CreateQualificationAction.Params.Submission(
                        id = submission.id,
                        requirementResponses = submission.requirementResponses
                            .map { requirementResponse ->
                                CreateQualificationAction.Params.Submission.RequirementResponse(
                                    id = requirementResponse.id,
                                    value = requirementResponse.value,
                                    relatedCandidate = requirementResponse.relatedCandidate
                                        .let { organization ->
                                            CreateQualificationAction.Params.Submission.RequirementResponse.RelatedCandidate(
                                                id = organization?.id,
                                                name = organization?.name
                                            )
                                        },
                                    requirement = requirementResponse.requirement
                                        .let { requirement ->
                                            CreateQualificationAction.Params.Submission.RequirementResponse.Requirement(
                                                id = requirement?.id
                                            )
                                        }
                                )
                            }
                    )
                },
                tender = contextTender.let { tender ->
                    CreateQualificationAction.Params.Tender(
                        otherCriteria = tender.otherCriteria
                            .let { otherCriteria ->
                                CreateQualificationAction.Params.Tender.OtherCriteria(
                                    reductionCriteria = otherCriteria?.reductionCriteria,
                                    qualificationSystemMethods = otherCriteria?.qualificationSystemMethods
                                )
                            },
                        conversions = tender.conversions
                            .map { conversion ->
                                CreateQualificationAction.Params.Tender.Conversion(
                                    id = conversion.id,
                                    description = conversion.description,
                                    coefficients = conversion.coefficients
                                        .map { coefficient ->
                                            CreateQualificationAction.Params.Tender.Conversion.Coefficient(
                                                value = coefficient.value,
                                                id = coefficient.id,
                                                coefficient = coefficient.coefficient
                                            )
                                        },
                                    rationale = conversion.rationale,
                                    relatedItem = conversion.relatedItem,
                                    relatesTo = conversion.relatesTo
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
        result: Option<CreateQualificationAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.QUALIFICATION,
                    action = QualificationCommands.CreateQualification
                )
            )
        val newQualifications = data.map { qualification ->
            Qualification(
                id = qualification.id,
                scoring = qualification.scoring,
                date = qualification.date,
                relatedSubmission = qualification.relatedSubmission,
                status = qualification.status
            )
        }

        updateOutcomes(context, data)
        context.qualifications = Qualifications(values = newQualifications)

        return MaybeFail.none()
    }

    private fun updateOutcomes(context: CamundaGlobalContext, qualifications: CreateQualificationAction.Result) {
        val platformId = context.requestInfo.platformId
        val outcomes = context.outcomes ?: Outcomes()
        val details = outcomes[platformId] ?: Outcomes.Details()

        val updateDetails = details.copy(
            qualifications = qualifications.map {
                Outcomes.Details.Qualification(
                    id = it.id,
                    token = it.token
                )
            }
        )
        outcomes[platformId] = updateDetails
        context.outcomes = outcomes
    }
}
