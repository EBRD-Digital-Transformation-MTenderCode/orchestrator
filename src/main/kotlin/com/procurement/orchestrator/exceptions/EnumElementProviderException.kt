package com.procurement.orchestrator.exceptions

class EnumElementProviderException(enumType: String, value: String, values: String) : RuntimeException(
    "Unknown value for enumType $enumType: $value, Allowed values are $values"
) {
    val code: String = "00.00"
}
