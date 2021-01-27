package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.classification.Classification
import com.procurement.orchestrator.domain.model.requirement.RequirementId
import com.procurement.orchestrator.domain.model.tender.criteria.CriteriaSource
import com.procurement.orchestrator.domain.model.tender.criteria.Criterion
import com.procurement.orchestrator.domain.model.tender.criteria.CriterionId
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.Requirement
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroup
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroupId
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroups
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.Requirements
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class CreateCriteriaForProcuringEntityAction :
    FunctionalAction<CreateCriteriaForProcuringEntityAction.Params, CreateCriteriaForProcuringEntityAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "createCriteriaForProcuringEntity"
    override val target: Target<Result> = Target.plural()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,
        @field:JsonProperty("operationType") @param:JsonProperty("operationType") val operationType: OperationTypeProcess,
        @field:JsonProperty("criteria") @param:JsonProperty("criteria") val criteria: List<Criterion>
    ) {
        data class Criterion(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: CriterionId,
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

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

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

                @field:JsonProperty("requirements") @param:JsonProperty("requirements") val requirements: List<Requirement>
            ) {
                data class Requirement(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: RequirementId,
                    @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("description") @param:JsonProperty("description") val description: String?
                )
            }
        }
    }

    class Result(values: List<Criterion>) : List<Result.Criterion> by values, Serializable {

        data class Criterion(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: CriterionId,
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
            @field:JsonProperty("source") @param:JsonProperty("source") val source: CriteriaSource,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

            @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("requirementGroups") @param:JsonProperty("requirementGroups") val requirementGroups: List<RequirementGroup>
        ) : Serializable {


            data class Classification(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String
            ) : Serializable

            data class RequirementGroup(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: RequirementGroupId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

                @field:JsonProperty("requirements") @param:JsonProperty("requirements") val requirements: Requirements
            ) : Serializable
        }
    }
}

fun CreateCriteriaForProcuringEntityAction.Result.Criterion.convertToGlobalContextEntity(): Criterion {

    val requirementGroups = this.requirementGroups
        .map { it.convertToGlobalContextEntity() }

    return Criterion(
        id = this.id,
        description = this.description,
        source = this.source,
        title = this.title,
        classification = this.classification.convertToGlobalContextEntity(),
        requirementGroups = RequirementGroups(requirementGroups)
    )
}

private fun CreateCriteriaForProcuringEntityAction.Result.Criterion.RequirementGroup.convertToGlobalContextEntity() =
    RequirementGroup(
        id = this.id,
        description = this.description,
        requirements = Requirements(this.requirements)
    )

private fun CreateCriteriaForProcuringEntityAction.Result.Criterion.Classification.convertToGlobalContextEntity() =
    Classification(
        id = this.id,
        scheme = this.scheme
    )

fun Criterion.convertToRequestEntity(): CreateCriteriaForProcuringEntityAction.Params.Criterion {
    val requirementGroups = this.requirementGroups
        .map { it.convertToRequestEntity() }

    val classification = this.classification?.convertToRequestEntity()

    return CreateCriteriaForProcuringEntityAction.Params.Criterion(
        id = this.id,
        title = this.title!!,
        description = this.description,
        requirementGroups = requirementGroups,
        classification = classification
    )
}

private fun Classification.convertToRequestEntity(): CreateCriteriaForProcuringEntityAction.Params.Criterion.Classification =
    CreateCriteriaForProcuringEntityAction.Params.Criterion.Classification(
        id = this.id,
        scheme = this.scheme
    )

private fun RequirementGroup.convertToRequestEntity(): CreateCriteriaForProcuringEntityAction.Params.Criterion.RequirementGroup {
    val requirements = this.requirements
        .map { it.convertToRequestEntity() }

    return CreateCriteriaForProcuringEntityAction.Params.Criterion.RequirementGroup(
        id = this.id,
        description = this.description,
        requirements = requirements
    )
}

private fun Requirement.convertToRequestEntity(): CreateCriteriaForProcuringEntityAction.Params.Criterion.RequirementGroup.Requirement =
    CreateCriteriaForProcuringEntityAction.Params.Criterion.RequirementGroup.Requirement(
        id = this.id,
        title = this.title,
        description = this.description
    )
