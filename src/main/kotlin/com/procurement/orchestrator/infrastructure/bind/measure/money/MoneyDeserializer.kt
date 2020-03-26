package com.procurement.orchestrator.infrastructure.bind.measure.money

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.node.ObjectNode
import com.procurement.orchestrator.domain.fail.error.DataValidationErrors
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.measure.Money
import com.procurement.orchestrator.infrastructure.exception.measure.MoneyParseException
import com.procurement.orchestrator.infrastructure.extension.jackson.tryGetBigDecimalAttribute
import com.procurement.orchestrator.infrastructure.extension.jackson.tryGetTextAttribute
import java.math.BigDecimal

class MoneyDeserializer : JsonDeserializer<Money>() {
    companion object {
        fun deserialize(node: ObjectNode): Money {
            val amount = getAmount(node)
                .orThrow { error -> MoneyParseException(error.message) }
            val currency = getCurrency(node)
                .orThrow { error -> MoneyParseException(error.message) }
            return Money.tryCreate(amount = amount, currency = currency)
                .orThrow { error -> MoneyParseException(error.message) }
        }

        private fun getAmount(node: ObjectNode): Result<BigDecimal, DataValidationErrors> =
            node.tryGetBigDecimalAttribute("amount")

        private fun getCurrency(node: ObjectNode): Result<String, DataValidationErrors> = node.tryGetTextAttribute("currency")
    }

    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): Money =
        deserialize(jsonParser.readValueAsTree())
}
