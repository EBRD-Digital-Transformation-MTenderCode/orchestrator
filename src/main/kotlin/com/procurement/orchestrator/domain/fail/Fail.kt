package com.procurement.orchestrator.domain.fail

import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.ValidationResult
import com.procurement.orchestrator.infrastructure.configuration.property.GlobalProperties

sealed class Fail(prefix: String, number: String) {
    companion object {
        const val ATTRIBUTE_NAME_KEY = "attributeName"
        const val ATTRIBUTE_PATH_KEY = "attributePath"
        const val ATTRIBUTE_ID_KEY = "attributeId"
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

        sealed class Bpms(number: String, description: String) :
            Incident(level = Level.ERROR, number = "2.$number", description = description) {

            sealed class Context(number: String, val name: String, val path: String?, description: String) :
                Bpms(number = "1.$number", description = description) {

                companion object {
                    private const val TOP_LEVEL_PATH = "at the top-level"
                }

                override fun logging(logger: Logger) {
                    val mdc = mutableMapOf<String, String>()
                        .apply {
                            put(ATTRIBUTE_NAME_KEY, name)
                            put(ATTRIBUTE_PATH_KEY, path ?: TOP_LEVEL_PATH)

                        }
                    logger.error(message, mdc = mdc)
                }

                sealed class UnConsistency(
                    number: String,
                    name: String,
                    path: String?,
                    val id: String? = null,
                    description: String
                ) : Context(number = "1.$number", name = name, path = path, description = description) {

                    override fun logging(logger: Logger) {
                        val mdc = mutableMapOf<String, String>()
                            .apply {
                                put(ATTRIBUTE_NAME_KEY, name)
                                put(ATTRIBUTE_PATH_KEY, path ?: TOP_LEVEL_PATH)
                                if (id != null) put(ATTRIBUTE_ID_KEY, id)
                            }
                        logger.error(message, mdc = mdc)
                    }

                    class Update(name: String, path: String? = null, id: String? = null) :
                        UnConsistency(
                            number = "1",
                            name = name,
                            path = path,
                            id = id,
                            description = "${name.capitalize()} ${id ?: ""}by the path '${path ?: TOP_LEVEL_PATH}' for an update is not found."
                        )
                }

                class Missing(name: String, path: String? = null) :
                    Context(
                        number = "2",
                        name = name,
                        path = path,
                        description = "The global context does not contain the '$name' attribute by the path '${path ?: TOP_LEVEL_PATH}'."
                    )

                class NotFoundElement(id: String, name: String, path: String? = null) :
                    Context(
                        number = "3",
                        name = name,
                        path = path,
                        description = "The element with id '${id}' in an attribute '$name' by the path '${path ?: TOP_LEVEL_PATH}' is not found in global context."
                    )

                class Empty(name: String, path: String? = null) :
                    Context(
                        number = "4",
                        name = name,
                        path = path,
                        description = "The Attribute '$name' by the path '${path ?: TOP_LEVEL_PATH}' is empty in global context."
                    )

                class ExpectedNumber(name: String, path: String? = null, expected: Int, actual: Int) :
                    Context(
                        number = "5",
                        name = name,
                        path = path,
                        description = "The attribute '$name' by the path '${path ?: TOP_LEVEL_PATH}' should have $expected element(s) in global context. Actually the attribute has $actual element(s)."
                    )

                class DataFormatMismatch(
                    name: String,
                    expectedFormat: String,
                    actualValue: String,
                    path: String? = null
                ) :
                    Context(
                        number = "6",
                        description = "Data format mismatch of '$name'. Expected data format: '$expectedFormat', actual value: '$actualValue'.",
                        name = name,
                        path = path
                    )

                class UnexpectedRelatedEntity(name: String, path: String? = null, expected: String, actual: String) :
                    Context(
                        number = "7",
                        description = "Unexpected related entity in parameter $name by path $path. Expected: $expected, actual: $actual.",
                        path = path,
                        name = name
                    )
            }
        }

        sealed class Bpmn(number: String, description: String) :
            Incident(level = Level.ERROR, number = "3.$number", description = description) {

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
        }

        sealed class Transform(number: String, description: String, val exception: Exception) :
            Incident(level = Level.ERROR, number = "4.$number", description = description) {

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
            Incident(level = Level.ERROR, number = "5", description = description)

        class BadResponse(description: String, val exception: Exception? = null, val body: String) :
            Incident(level = Level.ERROR, number = "6", description = description) {

            override fun logging(logger: Logger) {
                logger.error("$message Body: '$body'.")
            }
        }

        class ResponseError(description: String) :
            Incident(level = Level.ERROR, number = "7", description = description)

        sealed class Database(number: String, description: String) :
            Incident(level = Level.ERROR, number = "8.$number", description = description) {

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
            Incident(level = Level.ERROR, number = "9.$number", description = description) {

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

        sealed class Response(
            number: String,
            description: String,
            val exception: Exception? = null
        ) : Incident(level = Level.ERROR, number = "10.$number", description = description) {

            sealed class Validation(
                number: String,
                description: String,
                exception: Exception? = null,
                val id: String,
                val name: String
            ) : Response("1.$number", description, exception) {

                class DuplicateEntity(id: String, name: String) : Validation(
                    number = "1",
                    description = "$name '$id' is duplicated.",
                    id = id,
                    name = name
                )

                class MissingExpectedEntity(id: String, name: String) : Validation(
                    number = "2",
                    description = "$name '$id' is missing in received response.",
                    id = id,
                    name = name
                )

                class UnknownEntity(id: String, name: String) : Validation(
                    number = "3",
                    description = "Received $name '$id' is unknown.",
                    id = id,
                    name = name
                )
            }

            class Empty(description: String) : Response(number = "2", description = description) {

                constructor(service: String, action: ProceduralAction<*>)
                    : this("The response to the '${action.name}' command from the service '$service' is empty.")
            }
        }
    }
}

fun <T, E : Fail.Error> E.toResult(): Result<T, E> = Result.failure(this)

fun <E : Fail.Error> E.toValidationResult(): ValidationResult<E> = ValidationResult.error(this)
