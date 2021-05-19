package com.procurement.orchestrator.infrastructure.web.controller

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.PlatformId
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.PlatformRequest
import com.procurement.orchestrator.application.service.ProcessLauncher
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.fail.error.RequestErrors
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asFailure
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.infrastructure.extension.http.getOperationId
import com.procurement.orchestrator.infrastructure.extension.http.getPayload
import com.procurement.orchestrator.infrastructure.extension.http.getPlatformId
import com.procurement.orchestrator.infrastructure.extension.http.getToken
import com.procurement.orchestrator.infrastructure.web.extension.buildResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
class CreateConfirmationResponseController(
    private val logger: Logger,
    private val processLauncher: ProcessLauncher
) {

    companion object {
        private const val PROCESS_NAME = "createConfirmationResponse"
    }

    @PostMapping("/do/confirmation/{entity}/{cpid}/{ocid}/{entityId}")
    fun createConfirmationResponse(
        servlet: HttpServletRequest,
        @PathVariable cpid: String,
        @PathVariable ocid: String,
        @PathVariable entityId: String,
        @RequestParam("role") role: String
    ): ResponseEntity<String> = perform(servlet = servlet, cpid = cpid, ocid = ocid, entityId = entityId, role = role)
        .also { fail -> fail.logging(logger) }
        .buildResponse()
        .also { response ->
            if (logger.isDebugEnabled)
                logger.debug("Response: status '${response.statusCode}', body '${response.body}'.")
        }

    private fun perform(
        servlet: HttpServletRequest,
        cpid: String,
        ocid: String,
        entityId: String,
        role: String
    ): MaybeFail<Fail> {
        val request: PlatformRequest = buildRequest(servlet = servlet, cpid = cpid, ocid = ocid, entityId = entityId, role = role)
            .orReturnFail { return MaybeFail.fail(it) }
            .also { request ->
                if (logger.isDebugEnabled)
                    logger.debug("Request: platform '${request.platformId}', operation-id '${request.operationId}', uri '${servlet.requestURI}', payload '${request.payload}'.")
            }
        return processLauncher.launchWithContextByCpid(request)
    }

    private fun buildRequest(
        servlet: HttpServletRequest,
        cpid: String,
        ocid: String,
        entityId: String,
        role: String
    ): Result<PlatformRequest, RequestErrors> {

        val verifiedCpid = parseCpid(cpid)
            .orForwardFail { return it }

        val verifiedOcid = parseSingleStageOcid(ocid)
            .orForwardFail { return it }

        val platformId: PlatformId = servlet.getPlatformId()
            .orForwardFail { fail -> return fail }

        val operationId: OperationId = servlet.getOperationId()
            .orForwardFail { fail -> return fail }

        val token: Token = servlet.getToken()
            .orForwardFail { fail -> return fail }

        val payload: String = servlet.getPayload()
            .orForwardFail { fail -> return fail }

        val operationType = getOperationTypeByRole(role)
            .orForwardFail { fail -> return fail }

        return PlatformRequest(
            operationId = operationId,
            platformId = platformId,
            context = PlatformRequest.Context(
                cpid = verifiedCpid,
                ocid = verifiedOcid,
                id = entityId,
                token = token,
                owner = platformId,
                uri = servlet.requestURI,
                operationType = operationType,
                processName = PROCESS_NAME
            ),
            payload = payload
        ).asSuccess()
    }

    private fun getOperationTypeByRole(role: String): Result<OperationTypeProcess, RequestErrors.QueryParameter.UnknownValue> {
        val parsedRole = getParsedRole(role)
            .orForwardFail { fail -> return fail }

        return when (parsedRole) {
            Role.BUYER -> OperationTypeProcess.CREATE_CONFIRMATION_RESPONSE_BY_BUYER
            Role.INVITED_CANDIDATE -> OperationTypeProcess.CREATE_CONFIRMATION_RESPONSE_BY_INVITED_CANDIDATE
            Role.SUPPLIER -> OperationTypeProcess.CREATE_CONFIRMATION_RESPONSE_BY_SUPPLIER
        }.asSuccess()
    }

    private fun getParsedRole(role: String): Result<Role, RequestErrors.QueryParameter.UnknownValue> =
        Role.orNull(role)?.asSuccess()
            ?: RequestErrors.QueryParameter.UnknownValue(
                name = "role",
                expectedValues = Role.allowedElements.map { it.key },
                actualValue = role
            ).asFailure()


    enum class Role(@JsonValue override val key: String) : EnumElementProvider.Key {
        BUYER("buyer"),
        INVITED_CANDIDATE("invitedCandidate"),
        SUPPLIER("supplier");

        override fun toString(): String = key

        companion object : EnumElementProvider<Role>(info = info()) {

            @JvmStatic
            @JsonCreator
            fun creator(name: String) = Role.orThrow(name)
        }
    }
}
