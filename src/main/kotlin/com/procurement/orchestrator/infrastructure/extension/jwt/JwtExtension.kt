package com.procurement.orchestrator.infrastructure.extension.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success

const val PLATFORM_ID_CLAIM = "idPlatform"

fun decodeJWT(authHeader: String): Result<DecodedJWT, Exception> = try {
    success(JWT.decode(authHeader))
} catch (expected: Exception) {
    failure(expected)
}

fun DecodedJWT.getPlatformId(): String? = this.getClaim(PLATFORM_ID_CLAIM).asString()
