package com.procurement.orchestrator.domain.fail.error

import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.domain.fail.Fail

sealed class BpeErrors(prefix: String, number: String, override val description: String) :
    Fail.Error(prefix = prefix, number = number) {

    override fun logging(logger: Logger) {
        logger.error(message = message)
    }

    class ImpossibleOperation(description: String) : BpeErrors(prefix = "SR-", number = "2", description = description)

    class Process(description: String) : BpeErrors(prefix = "VR-", number = "16", description = description)
}
