package com.procurement.orchestrator.domain.fail

import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.ValidationResult
import com.procurement.orchestrator.infrastructure.configuration.property.GlobalProperties

sealed class Fail(prefix: String, number: String) {
    companion object {
        const val ATTRIBUTE_NAME_KEY = "attributeName"
        const val REQUEST_HEADER_NAME = "headerName"
        const val REQUEST_QUERY_PARAMETER_NAME = "queryParameterName"
        const val REQUEST_PAYLOAD = "payload"
    }

    val code: String = "$prefix$number/${GlobalProperties.service.id}"
    abstract val description: String
    val message: String
        get() = "ERROR CODE: '$code', DESCRIPTION: '$description'."

    abstract fun logging(logger: Logger)

    override fun toString(): String = description

    abstract class Error(prefix: String, number: String) : Fail(prefix = prefix, number = number)

    sealed class Incident(val level: Level, number: String, override val description: String) :
        Fail(prefix = "INC-", number = number) {

        override fun logging(logger: Logger) {
            when (level) {
                Level.ERROR -> logger.error(message)
                Level.WARNING -> logger.warn(message)
                Level.INFO -> logger.info(message)
            }
        }

        class Bpe(description: String, val exception: Exception? = null) :
            Incident(level = Level.ERROR, number = "1", description = description) {

            override fun logging(logger: Logger) {
                logger.error(message = message, exception = exception)
            }
        }

        sealed class Bpmn(number: String, description: String) :
            Incident(level = Level.ERROR, number = "2.$number", description = description) {

            sealed class Parameter(number: String, description: String) :
                Bpmn(number = "1.$number", description = description) {

                class MissingRequired(name: String) :
                    Parameter(number = "1", description = "Missing required delegate property '$name'.")

                class UnknownValue(name: String, expectedValues: Collection<String>, actualValue: String) :
                    Parameter(
                        number = "2",
                        description = "Property '$name' has value mismatch with one of enum expected values. Expected values: '${expectedValues.joinToString()}', actual value: '$actualValue'."
                    )

                class UnConsistency(description: String) : Parameter(number = "3", description = description)

                class DataTypeMismatch(name: String, expectedType: String, actualType: String) :
                    Parameter(
                        number = "4",
                        description = "Data type a property '$name' is a mismatch. Expected data type: '$expectedType', actual data type: '$actualType'."
                    )
            }

            sealed class Context(number: String, description: String) :
                Bpmn(number = "2.$number", description = description) {

                class UnConsistency(val name: String, description: String) :
                    Context(number = "1", description = description) {

                    override fun logging(logger: Logger) {
                        logger.error(message, mdc = mapOf(ATTRIBUTE_NAME_KEY to name))
                    }
                }

                class Missing(name: String, path: String? = null) :
                    Context(
                        number = "2",
                        description = "The global context does not contain the '$name' attribute ${if (path != null) "by the path '$path'." else "at the top-level."}"
                    )

                class NotFoundElement(id: String, path: String) :
                    Context(
                        number = "3",
                        description = "The element with id '${id}' by the path '$path' is not found in global context."
                    )

                class Empty(path: String) :
                    Context(number = "4", description = "Attribute by the path '$path' is empty in global context.")
            }
        }

        sealed class Transform(number: String, description: String, val exception: Exception) :
            Incident(level = Level.ERROR, number = "3.$number", description = description) {

            override fun logging(logger: Logger) {
                logger.error(message = message, exception = exception)
            }

            class Parsing(description: String, exception: Exception) :
                Transform(number = "1", description = description, exception = exception)

            class Mapping(description: String, exception: Exception) :
                Transform(number = "2", description = description, exception = exception)

            class Deserialization(description: String, exception: Exception) :
                Transform(number = "3", description = description, exception = exception)

            class Serialization(description: String, exception: Exception) :
                Transform(number = "4", description = description, exception = exception)
        }

        class NetworkError(description: String) :
            Incident(level = Level.ERROR, number = "4", description = description)

        class BadResponse(description: String, val exception: Exception? = null, val body: String) :
            Incident(level = Level.ERROR, number = "5", description = description) {

            override fun logging(logger: Logger) {
                logger.error("$message Body: '$body'.")
            }
        }

        class ResponseError(description: String) :
            Incident(level = Level.ERROR, number = "6", description = description)

        sealed class Database(number: String, description: String) :
            Incident(level = Level.ERROR, number = "7.$number", description = description) {

            class Access(description: String, val exception: Exception) :
                Database(number = "1", description = description) {

                override fun logging(logger: Logger) {
                    logger.error(message = message, exception = exception)
                }
            }

            class Data(description: String) : Database(number = "2", description = description)

            class Consistency(description: String) : Database(number = "3", description = description)
        }

        sealed class Bus(number: String, description: String) :
            Incident(level = Level.ERROR, number = "8.$number", description = description) {

            class Producer(description: String, val exception: Exception) :
                Bus(number = "1", description = description) {

                override fun logging(logger: Logger) {
                    logger.error(message = message, exception = exception)
                }
            }
        }

        enum class Level(override val key: String) : EnumElementProvider.Key {
            ERROR("error"),
            WARNING("warning"),
            INFO("info");

            companion object : EnumElementProvider<Level>(info = info())
        }
    }
}

fun <T, E : Fail.Error> E.toResult(): Result<T, E> = Result.failure(this)

fun <E : Fail.Error> E.toValidationResult(): ValidationResult<E> = ValidationResult.error(this)
