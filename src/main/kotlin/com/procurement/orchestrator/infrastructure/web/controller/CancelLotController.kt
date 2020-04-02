package com.procurement.orchestrator.infrastructure.web.controller

import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.cancellation.CancellationLot
import com.procurement.orchestrator.application.service.cancellation.CancellationService
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.fail.error.RequestErrors
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.infrastructure.extension.http.getOperationId
import com.procurement.orchestrator.infrastructure.extension.http.getPayload
import com.procurement.orchestrator.infrastructure.extension.http.getPlatformId
import com.procurement.orchestrator.infrastructure.extension.http.getToken
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/cancel/lot")
class CancelLotController(
    private val logger: Logger,
    private val cancellationService: CancellationService
) {

    @PostMapping("/{cpid}/{ocid}/{lotId}")
    fun cancelTender(
        servlet: HttpServletRequest,
        @PathVariable cpid: String,
        @PathVariable ocid: String,
        @PathVariable lotId: String
    ): ResponseEntity<String> {

        val request = parseRequest(servlet, cpid = cpid, ocid = ocid, lotId = lotId)
            .orReturnFail { fail ->
                fail.logging(logger)
                return ResponseEntity(fail.message, HttpStatus.BAD_REQUEST)
            }

        return when (val result: MaybeFail<Fail> = cancellationService.cancelLot(request)) {
            is MaybeFail.Fail -> when (val error = result.error) {
                is Fail.Error -> {
                    error.logging(logger)
                    ResponseEntity(error.message, HttpStatus.BAD_REQUEST)
                }
                is Fail.Incident -> {
                    error.logging(logger)
                    ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
                }
            }
            is MaybeFail.None -> ResponseEntity("ok", HttpStatus.ACCEPTED)
        }
    }

    private fun parseRequest(
        servlet: HttpServletRequest,
        cpid: String,
        ocid: String,
        lotId: String
    ): Result<CancellationLot.Request, RequestErrors> {

        val verifiedCpid = Cpid.tryCreateOrNull(cpid)
            ?: return failure(
                RequestErrors.Common.DataFormatMismatch(
                    name = "cpid",
                    expectedFormat = Cpid.pattern,
                    actualValue = cpid
                )
            )

        val verifiedOcid = Ocid.tryCreateOrNull(ocid)
            ?: return failure(
                RequestErrors.Common.DataFormatMismatch(
                    name = "ocid",
                    expectedFormat = Ocid.pattern,
                    actualValue = ocid
                )
            )

        val verifiedLotId = LotId.Permanent.tryCreateOrNull(lotId)
            ?: return failure(
                RequestErrors.Common.DataFormatMismatch(
                    name = "lot-id",
                    expectedFormat = LotId.Permanent.pattern,
                    actualValue = lotId
                )
            )

        val platformId = servlet.getPlatformId()
            .orReturnFail { return failure(it) }

        val operationId: OperationId = servlet.getOperationId()
            .orReturnFail { return failure(it) }

        val token = servlet.getToken()
            .orReturnFail { return failure(it) }

        val payload = servlet.getPayload()
            .orReturnFail { return failure(it) }

        return CancellationLot
            .Request(
                operationId = operationId,
                platformId = platformId,
                context = CancellationLot.Request.Context(
                    cpid = verifiedCpid,
                    ocid = verifiedOcid,
                    token = token,
                    lotId = verifiedLotId
                ),
                payload = payload
            )
            .asSuccess()
    }
}
