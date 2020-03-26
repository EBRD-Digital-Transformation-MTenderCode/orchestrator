package com.procurement.orchestrator.domain.model.requirement

import java.math.BigDecimal

sealed class RequirementResponseValue {
    data class AsBoolean(val value: Boolean) : RequirementResponseValue()
    data class AsString(val value: String) : RequirementResponseValue()
    data class AsNumber(val value: BigDecimal) : RequirementResponseValue()
    data class AsInteger(val value: Long) : RequirementResponseValue()
}
