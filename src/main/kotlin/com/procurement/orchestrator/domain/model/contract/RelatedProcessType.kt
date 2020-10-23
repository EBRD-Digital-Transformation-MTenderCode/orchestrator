package com.procurement.orchestrator.domain.model.contract

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class RelatedProcessType(@JsonValue override val key: String) : EnumElementProvider.Key {

    FRAMEWORK("framework"),
    PARENT("parent"),
    PLANNING("planning"),
    X_CONTRACTING("x_contracting"),
    X_DEMAND("x_demand"),
    X_EVALUATION("x_evaluation"),
    X_EXECUTION("x_execution"),
    X_EXPENDITURE_ITEM("x_expenditureItem"),
    X_FUNDING_SOURCE("x_fundingSource"),
    X_NEGOTIATION("x_negotiation"),
    X_PLANNED("x_planned"),
    X_PRE_AWARD_CATALOG_REQUEST("x_preAwardCatalogRequest"),
    X_PRE_QUALIFICATION("x_prequalification"),
    X_PRESELECTION("x_preselection"),
    X_SCOPE("x_scope");

    override fun toString(): String = key

    companion object : EnumElementProvider<RelatedProcessType>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
