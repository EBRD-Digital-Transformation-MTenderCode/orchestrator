package com.procurement.orchestrator.infrastructure.client.web.qualification.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.measure.Scoring
import com.procurement.orchestrator.domain.model.organization.OrganizationId
import com.procurement.orchestrator.domain.model.qualification.QualificationId
import com.procurement.orchestrator.domain.model.qualification.QualificationStatus
import com.procurement.orchestrator.domain.model.requirement.RequirementId
import com.procurement.orchestrator.domain.model.requirement.RequirementResponseValue
import com.procurement.orchestrator.domain.model.requirement.RequirementStatus
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId
import com.procurement.orchestrator.domain.model.submission.SubmissionId
import com.procurement.orchestrator.domain.model.tender.conversion.ConversionId
import com.procurement.orchestrator.domain.model.tender.conversion.ConversionsRelatesTo
import com.procurement.orchestrator.domain.model.tender.conversion.coefficient.CoefficientId
import com.procurement.orchestrator.domain.model.tender.conversion.coefficient.CoefficientRate
import com.procurement.orchestrator.domain.model.tender.conversion.coefficient.CoefficientValue
import com.procurement.orchestrator.domain.model.tender.criteria.CriteriaRelatesTo
import com.procurement.orchestrator.domain.model.tender.criteria.CriteriaSource
import com.procurement.orchestrator.domain.model.tender.criteria.CriterionId
import com.procurement.orchestrator.domain.model.tender.criteria.QualificationSystemMethod
import com.procurement.orchestrator.domain.model.tender.criteria.ReductionCriteria
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementDataType
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroupId
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class CreateQualificationAction : FunctionalAction<CreateQualificationAction.Params, CreateQualificationAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "createQualifications"
    override val target: Target<Result> = Target.plural()

    class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime,
        @param:JsonProperty("owner") @field:JsonProperty("owner") val owner: Owner,
        @param:JsonProperty("submissions") @field:JsonProperty("submissions") val submissions: List<Submission>,
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender
    ) {
        data class Submission(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: SubmissionId,
            @param:JsonProperty("requirementResponses") @field:JsonProperty("requirementResponses") val requirementResponses: List<RequirementResponse>
        ) {
            data class RequirementResponse(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementResponseId,
                @param:JsonProperty("requirement") @field:JsonProperty("requirement") val requirement: Requirement,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("value") @field:JsonProperty("value") val value: RequirementResponseValue?,
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("relatedCandidate") @field:JsonProperty("relatedCandidate") val relatedCandidate: RelatedCandidate?
            ) {
                data class Requirement(
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementId?
                )

                data class RelatedCandidate(
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: OrganizationId?,
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String?
                )
            }
        }

        data class Tender(
            @param:JsonProperty("otherCriteria") @field:JsonProperty("otherCriteria") val otherCriteria: OtherCriteria,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("conversions") @field:JsonProperty("conversions") val conversions: List<Conversion>?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("criteria") @param:JsonProperty("criteria") val criteria: List<Criterion>?
        ) {

            data class Criterion(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: CriterionId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("source") @param:JsonProperty("source") val source: CriteriaSource?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: CriteriaRelatesTo?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification?,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @field:JsonProperty("requirementGroups") @param:JsonProperty("requirementGroups") val requirementGroups: List<RequirementGroup>
            ) {

                data class Classification(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String
                )

                data class RequirementGroup(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: RequirementGroupId,

                    @field:JsonProperty("requirements") @param:JsonProperty("requirements") val requirements: List<Requirement>
                ) {
                    data class Requirement(
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: RequirementId,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("status") @param:JsonProperty("status") val status: RequirementStatus?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("dataType") @param:JsonProperty("dataType") val dataType: RequirementDataType?
                    )
                }
            }

            data class Conversion(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: ConversionId,
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("relatedItem") @field:JsonProperty("relatedItem") val relatedItem: String?,
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("relatesTo") @field:JsonProperty("relatesTo") val relatesTo: ConversionsRelatesTo?,
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("rationale") @field:JsonProperty("rationale") val rationale: String?,
                @param:JsonProperty("coefficients") @field:JsonProperty("coefficients") val coefficients: List<Coefficient>,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("description") @field:JsonProperty("description") val description: String?
            ) {
                data class Coefficient(
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("value") @field:JsonProperty("value") val value: CoefficientValue?,
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("coefficient") @field:JsonProperty("coefficient") val coefficient: CoefficientRate?,
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: CoefficientId
                )
            }

            data class OtherCriteria(
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("reductionCriteria") @field:JsonProperty("reductionCriteria") val reductionCriteria: ReductionCriteria?,
                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("qualificationSystemMethods") @field:JsonProperty("qualificationSystemMethods") val qualificationSystemMethods: List<QualificationSystemMethod>?
            )
        }
    }

    class Result(qualification: List<Qualification>) : List<Result.Qualification> by qualification, Serializable {
        class Qualification(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: QualificationId,
            @param:JsonProperty("status") @field:JsonProperty("status") val status: QualificationStatus,
            @param:JsonProperty("relatedSubmission") @field:JsonProperty("relatedSubmission") val relatedSubmission: SubmissionId,
            @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime,
            @param:JsonProperty("token") @field:JsonProperty("token") val token: Token,
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("scoring") @field:JsonProperty("scoring") val scoring: Scoring?
        ) : Serializable
    }
}
