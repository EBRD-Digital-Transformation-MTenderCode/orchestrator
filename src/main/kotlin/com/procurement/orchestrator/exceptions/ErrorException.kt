package com.procurement.orchestrator.exceptions

class ErrorException(val error: ErrorType, message: String? = null) : RuntimeException(
    when (message) {
        null -> error.message
        else -> error.message + " " + message
    }
) {
    val code: String = error.code
}
