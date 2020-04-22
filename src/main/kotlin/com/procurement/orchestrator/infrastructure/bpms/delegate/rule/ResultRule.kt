package com.procurement.orchestrator.infrastructure.bpms.delegate.rule

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.ValidationResult
import com.procurement.orchestrator.domain.functional.ValidationRule
import com.procurement.orchestrator.domain.util.extension.getDuplicate
import com.procurement.orchestrator.domain.util.extension.getMissingElements
import com.procurement.orchestrator.domain.util.extension.getUnknownElements

fun <T : Collection<Any>> duplicatesRule(attributeName: String): ValidationRule<T, Fail.Incident.Response.Validation.DuplicateEntity> =
    ValidationRule { received: T ->
        val duplicate = received.getDuplicate { it }
        if (duplicate != null)
            ValidationResult.error(
                Fail.Incident.Response.Validation.DuplicateEntity(
                    id = duplicate.toString(), name = attributeName
                )
            )
        else
            ValidationResult.ok()
    }

fun <T : Collection<Any>> unknownEntityRule(expectedValues: T, attributeName: String) =
    ValidationRule { received: T ->
        val unknownValues = getUnknownElements(received = received, known = expectedValues)
        if (unknownValues.isNotEmpty())
            ValidationResult.error(
                Fail.Incident.Response.Validation.UnknownEntity(
                    id = unknownValues.joinToString(), name = attributeName
                )
            )
        else
            ValidationResult.ok()
    }

fun <T : Collection<Any>> missingEntityRule(expectedValues: T, attributeName: String) =
    ValidationRule { received: T ->
        val missingValues = getMissingElements(received = received, known = expectedValues)
        if (missingValues.isNotEmpty())
            ValidationResult.error(
                Fail.Incident.Response.Validation.MissingExpectedEntity(
                    id = missingValues.joinToString(), name = attributeName
                )
            )
        else
            ValidationResult.ok()
    }