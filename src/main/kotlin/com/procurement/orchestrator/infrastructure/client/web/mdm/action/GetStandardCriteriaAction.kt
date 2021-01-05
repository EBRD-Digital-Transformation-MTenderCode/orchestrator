package com.procurement.orchestrator.infrastructure.client.web.mdm.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.classification.Classification
import com.procurement.orchestrator.domain.model.requirement.RequirementId
import com.procurement.orchestrator.domain.model.tender.ProcurementCategory
import com.procurement.orchestrator.domain.model.tender.criteria.Criterion
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.NoneValue
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.Requirement
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementDataType
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroup
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroupId
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroups
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.Requirements
import java.io.Serializable

abstract class GetStandardCriteriaAction {

    class Params(
        @field:JsonProperty("lang") @param:JsonProperty("lang") val lang: String,
        @field:JsonProperty("country") @param:JsonProperty("country") val country: String,
        @field:JsonProperty("mainProcurementCategory") @param:JsonProperty("mainProcurementCategory") val mainProcurementCategory: ProcurementCategory?,
        @field:JsonProperty("criteriaCategory") @param:JsonProperty("criteriaCategory") val criteriaCategory: String?
    )

    sealed class Response {

        class Success(
            @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("data") @param:JsonProperty("data") val data: List<Criterion>?
        ) : Response(), Serializable {

            data class Criterion(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("title") @field:JsonProperty("title") val title: String,
                @param:JsonProperty("classification") @field:JsonProperty("classification") val classification: Classification,

                @field:JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                @param:JsonProperty("requirementGroups") @field:JsonProperty("requirementGroups") val requirementGroups: List<RequirementGroup>
            ) : Serializable {
                data class Classification(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String
                ) : Serializable

                data class RequirementGroup(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementGroupId,

                    @field:JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                    @param:JsonProperty("requirements") @field:JsonProperty("requirements") val requirements: List<Requirement>
                ) : Serializable {
                    data class Requirement(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementId,
                        @param:JsonProperty("title") @field:JsonProperty("title") val title: String,
                        @field:JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                        @param:JsonProperty("dataType") @field:JsonProperty("dataType") val dataType: RequirementDataType
                    ) : Serializable
                }
            }
        }
    }
}

object GetStandardCriteria {
    sealed class Result {
        data class Success(val criteria: List<Criterion>) : Result() {
            data class Criterion(
                val id: String,
                val title: String,
                val classification: Classification,
                val description: String?,

                val requirementGroups: List<RequirementGroup>
            ) {
                data class Classification(
                    val id: String,
                    val scheme: String
                )

                data class RequirementGroup(
                    val id: RequirementGroupId,
                    val description: String?,
                    val requirements: List<Requirement>
                ) {
                    data class Requirement(
                        val id: RequirementId,
                        val title: String,
                        val description: String?,
                        val dataType: RequirementDataType
                    )
                }
            }
        }
    }
}

fun GetStandardCriteria.Result.Success.Criterion.convertToGlobalContextEntity(): Criterion =
    Criterion(
        id = this.id,
        title = this.title,
        description = this.description,
        classification = this.classification
            .let { classification ->
                Classification(
                    id = classification.id,
                    scheme = classification.scheme
                )
            },
        requirementGroups = this.requirementGroups.map { requirementGroup ->
            RequirementGroup(
                id = requirementGroup.id,
                description = requirementGroup.description,
                requirements = requirementGroup.requirements
                    .map { requirement ->
                    Requirement(
                        id = requirement.id,
                        title = requirement.title,
                        description = requirement.description,
                        dataType = requirement.dataType,
                        value = NoneValue,
                        period = null
                    )
                }
                    .let { Requirements(it) }
            )
        }.let { RequirementGroups(it) }
    )
