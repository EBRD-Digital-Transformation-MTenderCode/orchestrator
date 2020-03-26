package com.procurement.orchestrator.domain.model.measure

import com.procurement.orchestrator.domain.fail.error.DomainErrors
import com.procurement.orchestrator.domain.fail.toResult
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.validate
import com.procurement.orchestrator.domain.rule.negativeValidationRule
import com.procurement.orchestrator.domain.rule.scaleValidationRule
import com.procurement.orchestrator.domain.util.extension.qualifiedName
import java.io.Serializable
import java.math.BigDecimal
import java.math.RoundingMode

class Quantity private constructor(val value: BigDecimal) : Serializable {

    companion object {
        private val CLASS_NAME = Quantity.qualifiedName()
        private const val AVAILABLE_SCALE = 3

        operator fun invoke(value: String): Quantity = tryCreate(value)
            .orThrow { error -> throw IllegalArgumentException(error.message) }

        operator fun invoke(value: BigDecimal): Quantity = tryCreate(value)
            .orThrow { error -> throw IllegalArgumentException(error.message) }

        fun tryCreate(text: String): Result<Quantity, DomainErrors> = try {
            tryCreate(BigDecimal(text))
        } catch (expected: Exception) {
            DomainErrors.IncorrectValue(className = CLASS_NAME, value = text, reason = expected.message)
                .toResult()
        }

        fun tryCreate(value: BigDecimal): Result<Quantity, DomainErrors> = value
            .validate(onScaleValue)
            .validate(onNegativeValue)
            .map { Quantity(value = it.setScale(AVAILABLE_SCALE, RoundingMode.HALF_UP)) }

        private val onScaleValue = scaleValidationRule(className = CLASS_NAME, availableScale = AVAILABLE_SCALE)
        private val onNegativeValue = negativeValidationRule(className = CLASS_NAME)
    }

    operator fun plus(other: Quantity): Quantity = Quantity(value = value + other.value)

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is Quantity && this.value == other.value
        else
            true
    }

    override fun hashCode(): Int = value.hashCode()
}
