package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.classification.Classification
import com.procurement.orchestrator.domain.model.tender.criteria.CriteriaRelatesTo
import com.procurement.orchestrator.domain.model.tender.criteria.CriteriaSource
import com.procurement.orchestrator.domain.model.tender.criteria.Criterion
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroup
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroups
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.Requirements
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class FindCriteriaAction : FunctionalAction<FindCriteriaAction.Params, FindCriteriaAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "findCriteria"
    override val target: Target<Result> = Target.plural()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("source") @param:JsonProperty("source") val source: List<CriteriaSource>
    )

    class Result(values: List<Criterion>) : List<Result.Criterion> by values, Serializable {

        data class Criterion(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
            @field:JsonProperty("source") @param:JsonProperty("source") val source: CriteriaSource,
            @field:JsonProperty("requirementGroups") @param:JsonProperty("requirementGroups") val requirementGroups: List<RequirementGroup>,

            @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

            @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: CriteriaRelatesTo,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String?
        ) : Serializable {

            data class Classification(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String
            ): Serializable

            data class RequirementGroup(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

                @field:JsonProperty("requirements") @param:JsonProperty("requirements") val requirements: Requirements
            ) : Serializable
        }
    }
}


fun FindCriteriaAction.Result.Criterion.convertToContextEntity(): Criterion {
    val requirementGroups= this.requirementGroups
        .map { requirementGroup -> requirementGroup.convertToContextEntity() }

    return Criterion(
        id          = this.id,
        source      = this.source,
        description = this.description,
        title       = this.title,
        relatedItem = this.relatedItem,
        relatesTo   = this.relatesTo,
        classification = this.classification.convertToContextEntity(),
        requirementGroups = RequirementGroups(requirementGroups)
    )
}

private fun FindCriteriaAction.Result.Criterion.Classification.convertToContextEntity(): Classification  =
    Classification(
        id      = this.id,
        scheme  = this.scheme
    )

private fun FindCriteriaAction.Result.Criterion.RequirementGroup.convertToContextEntity(): RequirementGroup  =
    RequirementGroup(
        id           = this.id,
        description  = this.description,
        requirements = Requirements(this.requirements)
    )

