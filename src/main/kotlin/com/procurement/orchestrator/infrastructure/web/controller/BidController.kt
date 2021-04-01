package com.procurement.orchestrator.infrastructure.web.controller

import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.PlatformId
import com.procurement.orchestrator.application.model.Stage
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.PlatformRequest
import com.procurement.orchestrator.application.service.ProcessLauncher
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.fail.error.RequestErrors
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.infrastructure.extension.http.getOperationId
import com.procurement.orchestrator.infrastructure.extension.http.getPayload
import com.procurement.orchestrator.infrastructure.extension.http.getPlatformId
import com.procurement.orchestrator.infrastructure.web.extension.buildResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/do/bid")
class BidController(
    private val logger: Logger,
    private val processLauncher: ProcessLauncher
) {

    companion object {
        private const val PROCESS_NAME_SUBMIT_BID = "submitBid"
        private const val PROCESS_NAME_SUBMIT_BID_IN_PCR = "submitBidInPcr"
    }

    @PostMapping("/{cpid}/{ocid}")
    fun doBid(
        servlet: HttpServletRequest,
        @PathVariable cpid: String,
        @PathVariable ocid: String
    ): ResponseEntity<String> =
        perform(servlet = servlet, cpid = cpid, ocid = ocid)
            .also { fail -> fail.logging(logger) }
            .buildResponse()
            .also { response ->
                if (logger.isDebugEnabled)
                    logger.debug("Response: status '${response.statusCode}', body '${response.body}'.")
            }

    private fun perform(servlet: HttpServletRequest, cpid: String, ocid: String): MaybeFail<Fail> =
        buildRequest(servlet = servlet, cpid = cpid, ocid = ocid)
            .orReturnFail { return MaybeFail.fail(it) }
            .also { request ->
                if (logger.isDebugEnabled)
                    logger.debug("Request: platform '${request.platformId}', operation-id '${request.operationId}', uri '${servlet.requestURI}', payload '${request.payload}'.")
            }
            .let { request -> launch(request) }

    private fun buildRequest(
        servlet: HttpServletRequest,
        cpid: String,
        ocid: String
    ): Result<PlatformRequest, RequestErrors> {
        val verifiedCpid = parseCpid(cpid)
            .orForwardFail { return it }

        val verifiedOcid = parseSingleStageOcid(ocid)
            .orForwardFail { return it } as Ocid.SingleStage

        val platformId: PlatformId = servlet.getPlatformId()
            .orForwardFail { fail -> return fail }

        val operationId: OperationId = servlet.getOperationId()
            .orForwardFail { fail -> return fail }

        val payload: String = servlet.getPayload()
            .orForwardFail { fail -> return fail }

        val processName = getProcessName(verifiedOcid.stage)

        return PlatformRequest(
            operationId = operationId,
            platformId = platformId,
            context = PlatformRequest.Context(
                cpid = verifiedCpid,
                ocid = verifiedOcid,
                token = null,
                owner = platformId,
                uri = servlet.requestURI,
                processName = processName
            ),
            payload = payload
        ).asSuccess()
    }

    fun getProcessName(stage: Stage) = when (stage) {
        Stage.AC,
        Stage.AP,
        Stage.EI,
        Stage.EV,
        Stage.FE,
        Stage.FS,
        Stage.NP,
        Stage.PN,
        Stage.RQ,
        Stage.TP -> PROCESS_NAME_SUBMIT_BID
        Stage.PC -> PROCESS_NAME_SUBMIT_BID_IN_PCR
    }

    fun launch(request: PlatformRequest): MaybeFail<Fail> =
        when ((request.context.ocid as Ocid.SingleStage).stage) {
            Stage.AC,
            Stage.AP,
            Stage.EI,
            Stage.EV,
            Stage.FE,
            Stage.FS,
            Stage.NP,
            Stage.PN,
            Stage.RQ,
            Stage.TP -> processLauncher.launchWithContextByCpid(request)
            Stage.PC -> processLauncher.launchWithContextByOcid(request)
        }
}