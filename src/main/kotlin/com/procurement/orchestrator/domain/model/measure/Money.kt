package com.procurement.orchestrator.domain.model.measure

import com.procurement.orchestrator.domain.fail.error.DomainErrors
import com.procurement.orchestrator.domain.fail.toResult
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.functional.validate
import com.procurement.orchestrator.domain.rule.negativeValidationRule
import com.procurement.orchestrator.domain.rule.scaleValidationRule
import com.procurement.orchestrator.domain.util.extension.qualifiedName
import java.io.Serializable
import java.math.BigDecimal
import java.math.RoundingMode

class Money private constructor(val amount: BigDecimal, val currency: String) : Serializable {

    companion object {
        private val CLASS_NAME = Quantity.qualifiedName()
        private const val AVAILABLE_SCALE = 2

        operator fun invoke(amount: String, currency: String): Money = tryCreate(amount, currency)
            .orThrow { error -> throw IllegalArgumentException(error.message) }

        operator fun invoke(amount: BigDecimal, currency: String): Money = tryCreate(amount, currency)
            .orThrow { error -> throw IllegalArgumentException(error.message) }

        fun tryCreate(amount: String, currency: String): Result<Money, DomainErrors> = try {
            tryCreate(BigDecimal(amount), currency)
        } catch (expected: Exception) {
            DomainErrors.IncorrectValue(className = CLASS_NAME, value = amount, reason = expected.message)
                .toResult()
        }

        fun tryCreate(amount: BigDecimal, currency: String): Result<Money, DomainErrors> {
            val amountResult = amount validate onScaleValue validate onNegativeValue

            if (amountResult.isFail)
                return failure(amountResult.error)

            return Money(
                amount = amountResult.get.setScale(AVAILABLE_SCALE, RoundingMode.HALF_UP),
                currency = currency
            ).asSuccess()
        }

        private val onScaleValue = scaleValidationRule(className = CLASS_NAME, availableScale = AVAILABLE_SCALE)
        private val onNegativeValue = negativeValidationRule(className = CLASS_NAME)
    }

    operator fun plus(other: Money): Money? = if (currency == other.currency)
        Money(amount = amount + other.amount, currency = currency)
    else
        null

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Money
            && this.amount == other.amount
            && this.currency == other.currency

    override fun hashCode(): Int {
        var result = currency.hashCode()
        result = 31 * result + amount.hashCode()
        return result
    }
}

inline fun <E : RuntimeException> Sequence<Money>.sum(notCompatibleCurrencyExceptionBuilder: () -> E): Money? =
    this.iterator().sum(notCompatibleCurrencyExceptionBuilder)

inline fun <E : RuntimeException> Iterable<Money>.sum(notCompatibleCurrencyExceptionBuilder: () -> E): Money? =
    this.iterator().sum(notCompatibleCurrencyExceptionBuilder)

inline fun <E : RuntimeException> Iterator<Money>.sum(notCompatibleCurrencyExceptionBuilder: () -> E): Money? {
    if (!this.hasNext()) return null
    val first: Money = this.next()
    var accumulator: BigDecimal = first.amount
    while (this.hasNext()) {
        val next: Money = this.next()
        if (first.currency != next.currency) throw notCompatibleCurrencyExceptionBuilder()
        accumulator += next.amount
    }
    return Money(
        amount = accumulator,
        currency = first.currency
    )
}
