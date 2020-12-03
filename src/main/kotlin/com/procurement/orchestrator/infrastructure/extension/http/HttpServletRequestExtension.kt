package com.procurement.orchestrator.infrastructure.extension.http

import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.PlatformId
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.error.RequestErrors
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asFailure
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.functional.bind
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.ProcurementMethodDetails
import com.procurement.orchestrator.domain.model.address.country.CountryId
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.infrastructure.extension.jwt.PLATFORM_ID_CLAIM
import com.procurement.orchestrator.infrastructure.extension.jwt.decodeJWT
import com.procurement.orchestrator.infrastructure.extension.jwt.getPlatformId
import com.procurement.orchestrator.infrastructure.web.controller.parseCpid
import com.procurement.orchestrator.infrastructure.web.controller.parseSingleStageOcid
import javax.servlet.http.HttpServletRequest

private const val HEADER_AUTHORIZATION = "Authorization"
private const val HEADER_OPERATION_ID = "X-OPERATION-ID"
private const val HEADER_TOKEN = "X-TOKEN"
private const val QUERY_PARAM_COUNTRY = "country"
private const val QUERY_PARAM_PMD = "pmd"
private const val LOT_ID = "lotId"
private const val QUERY_FA = "FA"
private const val QUERY_AP = "AP"
private const val QUERY_CP = "CP"
private const val QUERY_PN = "PN"

private const val AUTH_TOKEN_TYPE = "Bearer"
private const val START_AUTH_TOKEN_POSITION = AUTH_TOKEN_TYPE.length + 1

fun HttpServletRequest.getPlatformId(): Result<PlatformId, RequestErrors.Header> {
    val header: String = this.getRequiredHeader(HEADER_AUTHORIZATION)
        .orForwardFail { fail -> return fail }
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
        .orReturnFail { exception ->
            return failure(
                RequestErrors.Header.DataFormatMismatch(
                    name = HEADER_AUTHORIZATION,
                    expectedFormat = "access-token",
                    actualValue = header,
                    exception = exception
                )
            )
        }

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

fun HttpServletRequest.getAuthorizationHeader(): Result<String, RequestErrors.Header> =
    this.getRequiredHeader(HEADER_AUTHORIZATION)

fun HttpServletRequest.getOperationId(): Result<OperationId, RequestErrors.Header> {
    val header: String = this.getRequiredHeader(HEADER_OPERATION_ID)
        .orForwardFail { fail -> return fail }

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
        .orForwardFail { fail -> return fail }

    return Token.tryCreateOrNull(header)
        ?.let { operationId -> success(operationId) }
        ?: RequestErrors.Header.DataFormatMismatch(name = HEADER_TOKEN, expectedFormat = "uuid", actualValue = header)
            .asFailure()
}

fun HttpServletRequest.getCountry(): Result<CountryId, RequestErrors.QueryParameter> =
    this.getRequiredQueryParam(QUERY_PARAM_COUNTRY)

fun HttpServletRequest.getPmd(): Result<ProcurementMethodDetails, RequestErrors.QueryParameter> {
    val param: String = this.getRequiredQueryParam(QUERY_PARAM_PMD)
        .orForwardFail { fail -> return fail }

    return ProcurementMethodDetails.orNull(param)
        ?.let { success(it) }
        ?: RequestErrors.QueryParameter.UnknownValue(
            name = QUERY_PARAM_PMD,
            expectedValues = ProcurementMethodDetails.allowedElements.keysAsStrings(),
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

fun HttpServletRequest.getFA(): Result<Cpid, RequestErrors> =
    getRequiredQueryParam(QUERY_FA).bind { parseCpid(it) }

fun HttpServletRequest.getAP(): Result<Ocid, RequestErrors> =
    getRequiredQueryParam(QUERY_AP).bind { parseSingleStageOcid(it) }

fun HttpServletRequest.getCP(): Result<Cpid, RequestErrors> =
    getRequiredQueryParam(QUERY_CP).bind { parseCpid(it) }

fun HttpServletRequest.getPN(): Result<Ocid, RequestErrors> =
    getRequiredQueryParam(QUERY_PN).bind { parseSingleStageOcid(it) }

fun HttpServletRequest.getLotId(): Result<LotId, RequestErrors> =
    getRequiredQueryParam(LOT_ID)
        .orForwardFail { return it }
        .let { LotId.create(it) }
        .asSuccess()

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




