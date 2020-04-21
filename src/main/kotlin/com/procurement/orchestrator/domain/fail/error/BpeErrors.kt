package com.procurement.orchestrator.domain.fail.error

import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.domain.fail.Fail

sealed class BpeErrors(number: String, override val description: String) :
    Fail.Error(prefix = "SR-", number = number) {

    class Process(description: String) : BpeErrors(number = "2", description = description) {

        override fun logging(logger: Logger) {
            logger.error(message = message)
        }
    }
}
