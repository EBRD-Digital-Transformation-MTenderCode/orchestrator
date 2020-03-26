package com.procurement.orchestrator.infrastructure.extension.http

import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.PlatformId
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.domain.fail.error.RequestErrors
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asFailure
import com.procurement.orchestrator.domain.model.ProcurementMethod
import com.procurement.orchestrator.domain.model.address.country.CountryId
import com.procurement.orchestrator.infrastructure.extension.jwt.PLATFORM_ID_CLAIM
import com.procurement.orchestrator.infrastructure.extension.jwt.decodeJWT
import com.procurement.orchestrator.infrastructure.extension.jwt.getPlatformId
import javax.servlet.http.HttpServletRequest

private const val HEADER_AUTHORIZATION = "Authorization"
private const val HEADER_OPERATION_ID = "X-OPERATION-ID"
private const val HEADER_TOKEN = "X-TOKEN"
private const val QUERY_PARAM_COUNTRY = "country"
private const val QUERY_PARAM_PMD = "pmd"

private const val AUTH_TOKEN_TYPE = "Bearer"
private const val START_AUTH_TOKEN_POSITION = AUTH_TOKEN_TYPE.length + 1


fun HttpServletRequest.getPlatformId(): Result<PlatformId, RequestErrors.Header> {
    val header: String = this.getRequiredHeader(HEADER_AUTHORIZATION)
        .doOnError { return failure(it) }
        .get
        .let {
            if (it.startsWith(AUTH_TOKEN_TYPE))
                it.substring(START_AUTH_TOKEN_POSITION)
            else
                return failure(
                    RequestErrors.Header.DataTypeMismatch(
                        name = HEADER_AUTHORIZATION,
                        expectedType = AUTH_TOKEN_TYPE,
                        actualType = it
                    )
                )
        }

    val decodedJWT = decodeJWT(header)
        .doOnError { exception ->
            return failure(
                RequestErrors.Header.DataFormatMismatch(
                    name = HEADER_AUTHORIZATION,
                    expectedFormat = "access-token",
                    actualValue = header,
                    exception = exception
                )
            )
        }
        .get

    val platformId = decodedJWT.getPlatformId()
        ?: return failure(
            RequestErrors.Header.InvalidValue(
                name = HEADER_AUTHORIZATION,
                description = "Access token do not have the attribute '$PLATFORM_ID_CLAIM'."
            )
        )


    return PlatformId.tryCreateOrNull(platformId)
        ?.let { success(it) }
        ?: return failure(
            RequestErrors.Header.InvalidValue(
                name = HEADER_AUTHORIZATION,
                description = "The attribute '$PLATFORM_ID_CLAIM' in access token have invalid format."
            )
        )
}

fun HttpServletRequest.getOperationId(): Result<OperationId, RequestErrors.Header> {
    val header: String = this.getRequiredHeader(HEADER_OPERATION_ID)
        .doOnError { return failure(it) }
        .get

    return OperationId.tryCreateOrNull(header)
        ?.let { operationId -> success(operationId) }
        ?: RequestErrors.Header.DataFormatMismatch(
            name = HEADER_OPERATION_ID,
            expectedFormat = "uuid",
            actualValue = header
        )
            .asFailure()
}

fun HttpServletRequest.getToken(): Result<Token, RequestErrors.Header> {
    val header: String = this.getRequiredHeader(HEADER_TOKEN)
        .doOnError { return failure(it) }
        .get

    return Token.tryCreateOrNull(header)
        ?.let { operationId -> success(operationId) }
        ?: RequestErrors.Header.DataFormatMismatch(name = HEADER_TOKEN, expectedFormat = "uuid", actualValue = header)
            .asFailure()
}

fun HttpServletRequest.getCountry(): Result<CountryId, RequestErrors.QueryParameter> =
    this.getRequiredQueryParam(QUERY_PARAM_COUNTRY)

fun HttpServletRequest.getPmd(): Result<ProcurementMethod, RequestErrors.QueryParameter> {
    val param: String = this.getRequiredQueryParam(QUERY_PARAM_PMD)
        .doOnError { return failure(it) }
        .get

    return ProcurementMethod.orNull(param)
        ?.let { success(it) }
        ?: RequestErrors.QueryParameter.UnknownValue(
            name = QUERY_PARAM_PMD,
            expectedValues = ProcurementMethod.allowedValues,
            actualValue = param
        )
            .asFailure()
}

fun HttpServletRequest.getPayload(): Result<String, RequestErrors.Payload.Missing> = this.inputStream
    .use { stream ->
        stream.bufferedReader().readText()
    }
    .takeIf { it.isNotBlank() }
    ?.let { success(it) }
    ?: failure(RequestErrors.Payload.Missing())

private fun HttpServletRequest.getRequiredHeader(name: String): Result<String, RequestErrors.Header> =
    this.getHeader(name)
        ?.takeIf { it.isNotBlank() }
        ?.let { success(it) }
        ?: failure(RequestErrors.Header.MissingRequiredHeader(name = name))

private fun HttpServletRequest.getRequiredQueryParam(name: String): Result<String, RequestErrors.QueryParameter.MissingRequiredParameter> =
    this.getParameter(name)
        ?.takeIf { it.isNotBlank() }
        ?.let { success(it) }
        ?: failure(RequestErrors.QueryParameter.MissingRequiredParameter(name = name))




