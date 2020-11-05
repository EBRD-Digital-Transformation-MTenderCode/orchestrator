package com.procurement.orchestrator.infrastructure.web.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.PlatformId
import com.procurement.orchestrator.application.model.Stage
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.PlatformRequest
import com.procurement.orchestrator.application.service.ProcessLauncher
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.Context
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.fail.error.RequestErrors
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.infrastructure.extension.http.getAuthorizationHeader
import com.procurement.orchestrator.infrastructure.extension.http.getOperationId
import com.procurement.orchestrator.infrastructure.extension.http.getPayload
import com.procurement.orchestrator.infrastructure.extension.http.getPlatformId
import com.procurement.orchestrator.infrastructure.extension.http.getToken
import com.procurement.orchestrator.infrastructure.web.extension.buildResponse
import com.procurement.orchestrator.service.ProcessService
import com.procurement.orchestrator.service.RequestService
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
    private val transform: Transform,
    private val processLauncher: ProcessLauncher,
    private val processService: ProcessService,
    private val requestService: RequestService
) {

    companion object {
        private const val PROCESS_NAME_V1 = "createBid"
        private const val PROCESS_NAME_V2 = "submitBidInPcr"
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

    private fun perform(servlet: HttpServletRequest, cpid: String, ocid: String): MaybeFail<Fail> {
        val verifiedOcid = parseSingleStageOcid(ocid)
            .orReturnFail { return MaybeFail.fail(it) } as Ocid.SingleStage

        return when (verifiedOcid.stage) {
            Stage.PC -> launchProcessApiV2(servlet = servlet, cpid = cpid, verifiedOcid = verifiedOcid)
            Stage.AC,
            Stage.AP,
            Stage.EI,
            Stage.EV,
            Stage.FE,
            Stage.FS,
            Stage.NP,
            Stage.PN,
            Stage.TP -> launchProcessApiV1(servlet = servlet, cpid = cpid, ocid = verifiedOcid)
        }
    }

    private fun launchProcessApiV2(
        servlet: HttpServletRequest,
        cpid: String,
        verifiedOcid: Ocid
    ): MaybeFail<Fail> {
        val request: PlatformRequest = buildRequest(servlet = servlet, cpid = cpid, ocid = verifiedOcid)
            .orReturnFail { return MaybeFail.fail(it) }
            .also { request ->
                if (logger.isDebugEnabled)
                    logger.debug("Request: platform '${request.platformId}', operation-id '${request.operationId}', uri '${servlet.requestURI}', payload '${request.payload}'.")
            }
        return processLauncher.launchWithContextByOcid(request)
    }

    private fun buildRequest(
        servlet: HttpServletRequest,
        cpid: String,
        ocid: Ocid
    ): Result<PlatformRequest, RequestErrors> {
        val verifiedCpid = parseCpid(cpid)
            .orForwardFail { return it }

        val platformId: PlatformId = servlet.getPlatformId()
            .orForwardFail { fail -> return fail }

        val operationId: OperationId = servlet.getOperationId()
            .orForwardFail { fail -> return fail }

        val token: Token = servlet.getToken()
            .orForwardFail { fail -> return fail }

        val payload: String = servlet.getPayload()
            .orForwardFail { fail -> return fail }

        return PlatformRequest(
            operationId = operationId,
            platformId = platformId,
            context = PlatformRequest.Context(
                cpid = verifiedCpid,
                ocid = ocid,
                token = token,
                owner = platformId,
                uri = servlet.requestURI,
                processName = PROCESS_NAME_V2
            ),
            payload = payload
        ).asSuccess()
    }

    fun launchProcessApiV1(
        servlet: HttpServletRequest,
        cpid: String,
        ocid: Ocid.SingleStage
    ): MaybeFail<Fail> {
        val payload = servlet.getPayload()
            .orReturnFail { return MaybeFail.fail(it) }

        val data = transform.tryParse(payload)
            .orReturnFail { return MaybeFail.fail(Fail.Incident.Bpe(description = "Invalid payload '$payload'.")) }

        val operationId = servlet.getOperationId()
            .orReturnFail { return MaybeFail.fail(it) }
            .toString()

        val authorization = servlet.getAuthorizationHeader()
            .orReturnFail { return MaybeFail.fail(it) }

        try {
            validateAndLaunchV1(operationId, data, authorization, cpid, ocid)
        } catch (exception: Exception) {
            return MaybeFail.fail(
                Fail.Incident.Bpe(
                    description = exception.message ?: "Failed starting process '$PROCESS_NAME_V1'.'",
                    exception = exception
                )
            )
        }

        return MaybeFail.none()
    }

    private fun validateAndLaunchV1(
        operationId: String,
        data: JsonNode,
        authorization: String,
        cpid: String,
        ocid: Ocid.SingleStage
    ) {
        requestService.validate(operationId, data)
        val context: Context = requestService.getContextForUpdateByCpid(
            authorization,
            operationId,
            cpid,
            ocid.toString(),
            null,
            PROCESS_NAME_V1
        )
        context.operationType = PROCESS_NAME_V1
        requestService.saveRequestAndCheckOperation(context, data)
        val variables: MutableMap<String, Any> = HashMap()
        variables["operationType"] = PROCESS_NAME_V1
        val bidNode = data["bid"] as ObjectNode
        val relatedLotId = (bidNode["relatedLots"] as ArrayNode)[0].asText()
        variables["lotId"] = relatedLotId

        processService.startProcess(context, variables)
    }
}