package com.procurement.orchestrator.domain.fail.error

import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.domain.fail.Fail

sealed class DataValidationErrors(number: String, val name: String, override val description: String) :
    Fail.Error(prefix = "DR-", number = number) {

    override val exception: Exception? = null

    override fun logging(logger: Logger) {
        logger.error(message = message, mdc = mapOf(ATTRIBUTE_NAME_KEY to name))
    }

    class MissingRequiredAttribute(name: String) :
        DataValidationErrors(number = "1", description = "Missing required attribute '$name'.", name = name)

    class DataTypeMismatch(name: String, expectedType: String, actualType: String) :
        DataValidationErrors(
            number = "2",
            description = "Data type mismatch of attribute '$name'. Expected data type: '$expectedType', actual data type: '$actualType'.",
            name = name
        )

    class UnknownValue(name: String, expectedValues: Collection<String>, actualValue: String) :
        DataValidationErrors(
            number = "3",
            description = "Attribute value mismatch of '$name' with one of enum expected values. Expected values: '${expectedValues.joinToString()}', actual value: '$actualValue'.",
            name = name
        )

    class DataFormatMismatch(name: String, expectedFormat: String, actualValue: String) :
        DataValidationErrors(
            number = "4",
            description = "Data format mismatch of attribute '$name'. Expected data format: '$expectedFormat', actual value: '$actualValue'.",
            name = name
        )

    class DataMismatchToPattern(name: String, pattern: String, actualValue: String) :
        DataValidationErrors(
            number = "5",
            description = "Data mismatch of attribute '$name' to the pattern: '$pattern'. Actual value: '$actualValue'.",
            name = name
        )

    class UniquenessDataMismatch(name: String, value: String) :
        DataValidationErrors(
            number = "6",
            description = "Uniqueness data mismatch of attribute '$name': '$value'.",
            name = name
        )

    class InvalidNumberOfElementsInArray(name: String, min: Int? = null, max: Int? = null, actualLength: Int) :
        DataValidationErrors(
            number = "7",
            description = "Invalid number of objects in the array of attribute '$name'. Expected length from '${min ?: "none min"}' to '${max ?: "none max"}', actual length: '$actualLength'.",
            name = name
        )

    class InvalidStringLength(name: String, min: Int? = null, max: Int? = null, actualLength: Int) :
        DataValidationErrors(
            number = "8",
            description = "Invalid number of chars in string of attribute '$name'. Expected length from '${min ?: "none min"}' to '${max ?: "none max"}', actual length: '$actualLength'.",
            name = name
        )

    class EmptyObject(name: String) :
        DataValidationErrors(number = "9", description = "Attribute '$name' is an empty object.", name = name)

    class EmptyArray(name: String) :
        DataValidationErrors(number = "10", description = "Attribute '$name' is an empty array.", name = name)

    class EmptyString(name: String) :
        DataValidationErrors(number = "11", description = "Attribute '$name' is an empty string.", name = name)

    class UnexpectedAttribute(name: String) :
        DataValidationErrors(number = "12", description = "Unexpected attribute '$name'.", name = name)

}
