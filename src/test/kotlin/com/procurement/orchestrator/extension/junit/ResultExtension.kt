package com.procurement.orchestrator.extension.junit

import com.procurement.orchestrator.domain.functional.Result
import org.opentest4j.AssertionFailedError

fun <T, E> assertSuccess(block: () -> Result<T, E>): T = when (val result = block()) {
    is Result.Success -> result.get
    is Result.Failure -> throw AssertionFailedError("Expected result to be success, but result is failure: '${result.error}'.")
}

inline fun <T, E : Any, reified E2 : E> assertFailure(block: () -> Result<T, E>): E2 =
    when (val result = block()) {
        is Result.Success -> throw AssertionFailedError("Expected result to be failure, but result is success.")
        is Result.Failure -> {
            when (val error = result.error) {
                is E2 -> error
                else -> throw AssertionFailedError("Expected result to be failure '${E2::class.java.canonicalName}', but result is '${error::class.java.canonicalName}'.")
            }
        }
    }
