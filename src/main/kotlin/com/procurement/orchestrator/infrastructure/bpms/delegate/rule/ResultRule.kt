package com.procurement.orchestrator.infrastructure.bpms.delegate.rule

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.ValidationResult
import com.procurement.orchestrator.domain.functional.ValidationRule

fun <T : List<Any>> duplicatesRule(attributeName: String): ValidationRule<T, Fail.Incident.Response.Validation.DuplicateEntity> =
    ValidationRule { valuesToCheck: T ->
        val duplicates = valuesToCheck.groupingBy { it }
            .eachCount()
            .filter { it.value > 1 }

        if (duplicates.isNotEmpty())
            ValidationResult.error(
                Fail.Incident.Response.Validation.DuplicateEntity(
                    id = duplicates.keys.joinToString(), name = attributeName
                )
            )
        else
            ValidationResult.ok()
    }

fun <T : Set<Any>> unknownEntityRule(expectedValues: T, attributeName: String) =
    ValidationRule { valuesToCheck: T ->
        val unknownValues = valuesToCheck.minus(expectedValues)
        if (unknownValues.isNotEmpty())
            ValidationResult.error(
                Fail.Incident.Response.Validation.UnknownEntity(
                    id = unknownValues.joinToString(), name = attributeName
                )
            )
        else
            ValidationResult.ok()
    }

fun <T : Set<Any>> missingEntityRule(expectedValues: T, attributeName: String) =
    ValidationRule { valuesToCheck: T ->
        val missingValues = expectedValues.minus(valuesToCheck)
        if (missingValues.isNotEmpty())
            ValidationResult.error(
                Fail.Incident.Response.Validation.MissingExpectedEntity(
                    id = missingValues.joinToString(), name = attributeName
                )
            )
        else
            ValidationResult.ok()
    }