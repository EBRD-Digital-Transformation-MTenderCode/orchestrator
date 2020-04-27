package com.procurement.orchestrator.infrastructure.web.controller

import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.confirmation.ConfirmationAmendment
import com.procurement.orchestrator.application.service.confirmation.ConfirmationService
import com.procurement.orchestrator.domain.fail.error.RequestErrors
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.amendment.AmendmentId
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.infrastructure.extension.http.getOperationId
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
@RequestMapping("/confirm/amendment")
class ConfirmAmendmentController(
    private val logger: Logger,
    private val cancellationService: ConfirmationService
) {

    @PostMapping("/{cpid}/{ocid}/{amendmentId}")
    fun confirmAmendment(
        servlet: HttpServletRequest,
        @PathVariable cpid: String,
        @PathVariable ocid: String,
        @PathVariable amendmentId: String
    ): ResponseEntity<String> = run {
        val request = parseRequest(servlet = servlet, cpid = cpid, ocid = ocid, amendmentId = amendmentId)
            .orReturnFail { return@run MaybeFail.fail(it) }
            .also { request ->
                if (logger.isDebugEnabled)
                    logger.debug(message = "Request: platform '${request.platformId}', operation-id '${request.operationId}', uri '/confirm/amendment/$cpid/$ocid/$amendmentId'.")
            }
        cancellationService.confirmAmendment(request)
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
        amendmentId: String
    ): Result<ConfirmationAmendment.Request, RequestErrors> {

        val verifiedCpid = Cpid.tryCreateOrNull(cpid)
            ?: return failure(
                RequestErrors.Common.DataFormatMismatch(
                    name = "cpid",
                    expectedFormat = Cpid.pattern,
                    actualValue = cpid
                )
            )

        val verifiedOcid = Ocid.SingleStage.tryCreateOrNull(ocid)
            ?: return failure(
                RequestErrors.Common.DataFormatMismatch(
                    name = "ocid",
                    expectedFormat = Ocid.SingleStage.pattern,
                    actualValue = ocid
                )
            )

        val verifiedAmendmentId: AmendmentId = AmendmentId.Permanent.tryCreateOrNull(amendmentId)
            ?: return failure(
                RequestErrors.Common.DataFormatMismatch(
                    name = "amendment-id",
                    expectedFormat = LotId.Permanent.pattern,
                    actualValue = amendmentId
                )
            )

        val platformId = servlet.getPlatformId()
            .orForwardFail { fail -> return fail }

        val operationId: OperationId = servlet.getOperationId()
            .orForwardFail { fail -> return fail }

        val token = servlet.getToken()
            .orForwardFail { fail -> return fail }

        val owner = servlet.getPlatformId()
            .orForwardFail { fail -> return fail }

        return ConfirmationAmendment
            .Request(
                operationId = operationId,
                platformId = platformId,
                context = ConfirmationAmendment.Request.Context(
                    cpid = verifiedCpid,
                    ocid = verifiedOcid,
                    token = token,
                    owner = owner,
                    amendmentId = verifiedAmendmentId
                )
            )
            .asSuccess()
    }
}
