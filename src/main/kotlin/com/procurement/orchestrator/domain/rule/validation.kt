package com.procurement.orchestrator.domain.rule

import com.procurement.orchestrator.domain.fail.error.DomainErrors
import com.procurement.orchestrator.domain.fail.toValidationResult
import com.procurement.orchestrator.domain.functional.ValidationResult
import com.procurement.orchestrator.domain.functional.ValidationRule
import java.math.BigDecimal

fun scaleValidationRule(className: String, availableScale: Int) =
    ValidationRule { value: BigDecimal ->
        val scale = value.scale()
        if (scale >= availableScale)
            DomainErrors.InvalidScale(className = className, currentScale = scale, availableScale = availableScale)
                .toValidationResult()
        else
            ValidationResult.ok()
    }

fun negativeValidationRule(className: String) = ValidationRule { amount: BigDecimal ->
    if (amount < BigDecimal.ZERO)
        DomainErrors.IncorrectValue(className = className, value = amount, reason = "The value must not be negative.")
            .toValidationResult()
    else
        ValidationResult.ok()
}
