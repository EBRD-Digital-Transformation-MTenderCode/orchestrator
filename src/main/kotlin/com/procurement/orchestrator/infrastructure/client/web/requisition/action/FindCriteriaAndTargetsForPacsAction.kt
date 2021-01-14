package com.procurement.orchestrator.infrastructure.client.web.requisition.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.contract.observation.Observation
import com.procurement.orchestrator.domain.model.contract.observation.Observations
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.requirement.RequirementId
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.domain.model.tender.criteria.Criteria
import com.procurement.orchestrator.domain.model.tender.criteria.CriteriaRelatesTo
import com.procurement.orchestrator.domain.model.tender.criteria.Criterion
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.Requirement
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroup
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroupId
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroups
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.Requirements
import com.procurement.orchestrator.domain.model.tender.target.Targets
import com.procurement.orchestrator.domain.model.unit.Unit
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class FindCriteriaAndTargetsForPacsAction : FunctionalAction<FindCriteriaAndTargetsForPacsAction.Params, FindCriteriaAndTargetsForPacsAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "findCriteriaAndTargetsForPacs"
    override val target: Target<Result> = Target.single()

    class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender
    ) {
        class Tender(
            @param:JsonProperty("lots") @field:JsonProperty("lots") val lots: List<Lot>
        ) {
            class Lot(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: LotId
            )
        }
    }

    class Result(
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender?
    ) : Serializable {

        data class Tender(
            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("criteria") @field:JsonProperty("criteria") val criteria: List<Criteria>?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("targets") @field:JsonProperty("targets") val targets: List<Target>?
        ) : Serializable {

            data class Criteria(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("relatesTo") @field:JsonProperty("relatesTo") val relatesTo: CriteriaRelatesTo?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("relatedItem") @field:JsonProperty("relatedItem") val relatedItem: String?,

                @param:JsonProperty("requirementGroups") @field:JsonProperty("requirementGroups") val requirementGroups: List<RequirementGroup>
            ) : Serializable {

                data class RequirementGroup(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementGroupId,
                    @param:JsonProperty("requirements") @field:JsonProperty("requirements") val requirements: List<Requirement>
                ) : Serializable {

                    data class Requirement(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementId,
                        @param:JsonProperty("title") @field:JsonProperty("title") val title: String
                    ) : Serializable
                }
            }

            data class Target(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("observations") @field:JsonProperty("observations") val observations: List<Observation>
            ) : Serializable {

                data class Observation(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("unit") @field:JsonProperty("unit") val unit: Unit,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("relatedRequirementId") @field:JsonProperty("relatedRequirementId") val relatedRequirementId: String?
                ) : Serializable {

                    data class Unit(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                    ) : Serializable
                }
            }
        }
    }
}

fun FindCriteriaAndTargetsForPacsAction.Result.Tender.toDomain(): Tender {
    return Tender(
        criteria = criteria
            ?.map { criteria ->
                Criterion(
                    id = criteria.id,
                    title = criteria.title,
                    relatesTo = criteria.relatesTo,
                    relatedItem = criteria.relatedItem,
                    requirementGroups = criteria.requirementGroups.map { requirementGroup ->
                        RequirementGroup(
                            id = requirementGroup.id,
                            requirements = requirementGroup.requirements.map { requirement ->
                                Requirement(
                                    id = requirement.id,
                                    title = requirement.title,
                                    period = null,
                                    description = null,
                                    dataType = null
                                )
                            }
                                .let { Requirements(it) }
                        )
                    }
                        .let { RequirementGroups(it) }
                )
            }
            .orEmpty()
            .let { Criteria(it) },
        targets = targets?.map { target ->
            com.procurement.orchestrator.domain.model.tender.target.Target(
                id = target.id,
                observations = target.observations.map { observation ->
                    Observation(
                        id = observation.id,
                        unit = observation.unit.let { unit ->
                            Unit(
                                id = unit.id,
                                name = unit.name
                            )
                        },
                        relatedRequirementId = observation.relatedRequirementId
                    )
                }.let { Observations(it) }
            )
        }
            .orEmpty()
            .let { Targets(it) }
    )
}
