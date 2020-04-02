package com.procurement.orchestrator.infrastructure.web.controller

import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.response.RequirementResponseDataIn
import com.procurement.orchestrator.application.service.response.RequirementResponseService
import com.procurement.orchestrator.domain.fail.Fail
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
import org.springframework.http.HttpStatus
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
    ): ResponseEntity<String> {

        val request = parseRequest(servlet, cpid = cpid, ocid = ocid, awardId = awardId)
            .doOnError { fail ->
                fail.logging(logger)
                return ResponseEntity(fail.message, HttpStatus.BAD_REQUEST)
            }
            .get

        return when (val result: MaybeFail<Fail> = requirementResponseService.declareNoConflictOfInterest(request)) {
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

        val verifiedOcid = Ocid.tryCreateOrNull(ocid)
            ?: return Result.failure(
                RequestErrors.Common.DataFormatMismatch(
                    name = "ocid",
                    expectedFormat = Ocid.pattern,
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
            .doOnError { return Result.failure(it) }
            .get

        val operationId: OperationId = servlet.getOperationId()
            .doOnError { return Result.failure(it) }
            .get

        val token = servlet.getToken()
            .doOnError { return Result.failure(it) }
            .get

        val payload = servlet.getPayload()
            .doOnError { return Result.failure(it) }
            .get

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
