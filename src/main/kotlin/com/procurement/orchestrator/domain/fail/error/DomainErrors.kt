package com.procurement.orchestrator.domain.fail.error

import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.domain.fail.Fail

sealed class DomainErrors(numberError: String, override val description: String) :
    Fail.Error(prefix = "DE-", number = numberError) {

    override val exception: Exception? = null

    override fun logging(logger: Logger) {
        logger.error(message)
    }

    class InvalidScale(className: String, currentScale: Int, availableScale: Int) : DomainErrors(
        numberError = "1",
        description = "Unable to instantiate '${className}' class because passed invalid scale '$currentScale', the available scale is '$availableScale'."
    )

    class IncorrectValue(className: String, value: Any, reason: String? = "") : DomainErrors(
        numberError = "2",
        description = "Unable to instantiate '${className}' class because passed incorrect value '$value'. Reason: '$reason'."
    )
}
