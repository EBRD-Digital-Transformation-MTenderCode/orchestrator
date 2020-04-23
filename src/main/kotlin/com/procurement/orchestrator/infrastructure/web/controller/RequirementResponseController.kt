package com.procurement.orchestrator.infrastructure.web.controller

import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.response.RequirementResponseDataIn
import com.procurement.orchestrator.application.service.response.RequirementResponseService
import com.procurement.orchestrator.domain.fail.error.RequestErrors
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.award.AwardId
import com.procurement.orchestrator.infrastructure.extension.http.getOperationId
import com.procurement.orchestrator.infrastructure.extension.http.getPayload
import com.procurement.orchestrator.infrastructure.extension.http.getPlatformId
import com.procurement.orchestrator.infrastructure.extension.http.getToken
import com.procurement.orchestrator.infrastructure.web.extension.buildResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/do/response")
class RequirementResponseController(
    private val logger: Logger,
    private val requirementResponseService: RequirementResponseService
) {
    @PostMapping("/{cpid}/{ocid}/{awardId}")
    fun cancelTender(
        servlet: HttpServletRequest,
        @PathVariable cpid: String,
        @PathVariable ocid: String,
        @PathVariable awardId: String
    ): ResponseEntity<String> = run {
        val request = parseRequest(servlet = servlet, cpid = cpid, ocid = ocid, awardId = awardId)
            .orReturnFail { return@run MaybeFail.fail(it) }
            .also { request ->
                if (logger.isDebugEnabled)
                    logger.debug("Request: platform '${request.platformId}', operation-id '${request.operationId}', uri '/do/response/$cpid/$ocid/$awardId', payload '${request.payload}'.")
            }
        requirementResponseService.declareNoConflictOfInterest(request)
    }
        .also { fail -> fail.logging(logger) }
        .buildResponse()
        .also { response ->
            if (logger.isDebugEnabled)
                logger.debug("Response: status '${response.statusCode}', body '${response.body}'.")
        }

    private fun parseRequest(
        servlet: HttpServletRequest,
        cpid: String,
        ocid: String,
        awardId: String
    ): Result<RequirementResponseDataIn.Request, RequestErrors> {

        val verifiedCpid = Cpid.tryCreateOrNull(cpid)
            ?: return Result.failure(
                RequestErrors.Common.DataFormatMismatch(
                    name = "cpid",
                    expectedFormat = Cpid.pattern,
                    actualValue = cpid
                )
            )

        val verifiedOcid = Ocid.SingleStage.tryCreateOrNull(ocid)
            ?: return Result.failure(
                RequestErrors.Common.DataFormatMismatch(
                    name = "ocid",
                    expectedFormat = Ocid.SingleStage.pattern,
                    actualValue = ocid
                )
            )

        val verifiedAwardId = AwardId.tryCreateOrNull(awardId)
            ?: return Result.failure(
                RequestErrors.Common.DataFormatMismatch(
                    name = "awardId",
                    expectedFormat = AwardId.pattern,
                    actualValue = awardId
                )
            )
        val platformId = servlet.getPlatformId()
            .orForwardFail { fail -> return fail }

        val operationId: OperationId = servlet.getOperationId()
            .orForwardFail { fail -> return fail }

        val token = servlet.getToken()
            .orForwardFail { fail -> return fail }

        val payload = servlet.getPayload()
            .orForwardFail { fail -> return fail }

        return RequirementResponseDataIn
            .Request(
                operationId = operationId,
                platformId = platformId,
                context = RequirementResponseDataIn.Request.Context(
                    cpid = verifiedCpid,
                    ocid = verifiedOcid,
                    awardId = verifiedAwardId,
                    token = token
                ),
                payload = payload
            )
            .asSuccess()
    }
}
