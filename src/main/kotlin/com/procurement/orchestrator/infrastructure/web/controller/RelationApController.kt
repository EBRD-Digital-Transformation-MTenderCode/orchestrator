package com.procurement.orchestrator.infrastructure.web.controller

import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.PlatformId
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.PlatformRequest
import com.procurement.orchestrator.application.service.ProcessLauncher
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.fail.error.RequestErrors
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
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
class RelationApController(
    private val logger: Logger,
    private val processLauncher: ProcessLauncher
) {

    companion object {
        private const val PROCESS_NAME = "relationAP"
    }

    @PostMapping("/do/relation/{cpid}/{ocid}")
    fun create(
        servlet: HttpServletRequest,
        @PathVariable cpid: String,
        @PathVariable ocid: String,
        @RequestParam(name = "MS", required = false) ms: String
    ): ResponseEntity<String> = perform(servlet = servlet, cpid = cpid, ocid = ocid, ms = ms)
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
        ms: String
    ): MaybeFail<Fail> {
        val request: PlatformRequest = buildRequest(servlet = servlet, cpid = cpid, ocid = ocid, ms = ms)
            .orReturnFail { return MaybeFail.fail(it) }
            .also { request ->
                if (logger.isDebugEnabled)
                    logger.debug("Request: platform '${request.platformId}', operation-id '${request.operationId}', uri '${servlet.requestURI}', payload '${request.payload}'.")
            }
        return processLauncher.launch(request)
    }

    private fun buildRequest(
        servlet: HttpServletRequest,
        cpid: String,
        ocid: String,
        ms: String
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

        val msCpid = parseCpid(ms)
            .orForwardFail { return it }

        return PlatformRequest(
            operationId = operationId,
            platformId = platformId,
            context = PlatformRequest.Context(
                cpid = verifiedCpid,
                ocid = verifiedOcid,
                token = token,
                owner = platformId,
                uri = servlet.requestURI,
                processName = PROCESS_NAME,
                relatedProcess = PlatformRequest.Context.RelatedProcess(msCpid)
            ),
            payload = payload
        ).asSuccess()
    }
}
