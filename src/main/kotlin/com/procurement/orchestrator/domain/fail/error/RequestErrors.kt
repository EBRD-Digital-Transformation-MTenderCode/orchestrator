package com.procurement.orchestrator.domain.fail.error

import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.domain.fail.Fail

sealed class RequestErrors(number: String, override val description: String) :
    Fail.Error(prefix = "RQ-", number = number) {

    sealed class Common(number: String, description: String) :
        RequestErrors(number = "1.$number", description = description) {

        class Repeated(description: String = "This is a repeated request.") :
            Common(number = "1", description = description) {

            override fun logging(logger: Logger) {
                logger.error(message = message)
            }
        }

        class DataFormatMismatch(
            name: String,
            expectedFormat: String,
            actualValue: String,
            val exception: Exception? = null
        ) : Common(
            number = "2",
            description = "Data format mismatch of '$name'. Expected data format: '$expectedFormat', actual value: '$actualValue'."
        ) {

            override fun logging(logger: Logger) {
                logger.error(message = message, exception = exception)
            }
        }
    }

    sealed class Header(number: String, val name: String, description: String) :
        RequestErrors(number = "2.$number", description = description) {

        override fun logging(logger: Logger) {
            logger.error(message = message, mdc = mapOf(REQUEST_HEADER_NAME to name))
        }

        class MissingRequiredHeader(name: String) :
            Header(number = "1", description = "Missing required header '$name'.", name = name)

        class DataTypeMismatch(
            name: String,
            expectedType: String,
            actualType: String,
            val exception: Exception? = null
        ) : Header(
            number = "2",
            description = "Data type mismatch of header '$name'. Expected data type: '$expectedType', actual data type: '$actualType'.",
            name = name
        ) {
            override fun logging(logger: Logger) {
                logger.error(message = message, mdc = mapOf(REQUEST_HEADER_NAME to name), exception = exception)
            }
        }

        class InvalidValue(name: String, description: String) :
            Header(number = "3", description = description, name = name)

        class DataFormatMismatch(
            name: String,
            expectedFormat: String,
            actualValue: String,
            val exception: Exception? = null
        ) : Header(
            number = "4",
            description = "Data format mismatch of header '$name'. Expected data format: '$expectedFormat', actual value: '$actualValue'.",
            name = name
        ) {

            override fun logging(logger: Logger) {
                logger.error(message = message, mdc = mapOf(REQUEST_HEADER_NAME to name), exception = exception)
            }
        }
    }

    sealed class QueryParameter(number: String, val name: String, description: String) :
        RequestErrors(number = "3.$number", description = description) {

        override fun logging(logger: Logger) {
            logger.error(message = message, mdc = mapOf(REQUEST_QUERY_PARAMETER_NAME to name))
        }

        class MissingRequiredParameter(name: String) :
            QueryParameter(number = "1", description = "Missing required parameter '$name'.", name = name)

        class DataTypeMismatch(name: String, expectedType: String, actualType: String) : QueryParameter(
            number = "2",
            description = "Data type mismatch of parameter '$name'. Expected data type: '$expectedType', actual data type: '$actualType'.",
            name = name
        )

        class UnknownValue(name: String, expectedValues: Collection<String>, actualValue: String) : QueryParameter(
            number = "3",
            description = "Value of header '$name' mismatch with one of expected values. Expected values: '${expectedValues.joinToString()}', actual value: '$actualValue'.",
            name = name
        )

        class DataFormatMismatch(name: String, expectedFormat: String, actualValue: String) : QueryParameter(
            number = "4",
            description = "Data format mismatch of parameter '$name'. Expected data format: '$expectedFormat', actual value: '$actualValue'.",
            name = name
        )
    }

    sealed class Payload(number: String, description: String) :
        RequestErrors(number = "4.$number", description = description) {

        override fun logging(logger: Logger) {
            logger.error(message = message)
        }

        class Missing : Payload(number = "1", description = "Missing payload.")

        class Parsing(
            description: String = "An error of parsing payload.",
            val payload: String,
            val exception: Exception
        ) : Payload(number = "2", description = description) {

            override fun logging(logger: Logger) {
                logger.error(message = message, mdc = mapOf(REQUEST_PAYLOAD to payload), exception = exception)
            }
        }
    }
}
