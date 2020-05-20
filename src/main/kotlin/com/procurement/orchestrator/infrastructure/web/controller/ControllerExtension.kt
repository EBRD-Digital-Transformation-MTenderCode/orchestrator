package com.procurement.orchestrator.infrastructure.web.controller

import com.procurement.orchestrator.domain.fail.error.RequestErrors
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid

fun parseCpid(cpid: String): Result<Cpid, RequestErrors> = Cpid.tryCreateOrNull(cpid)
    ?.asSuccess()
    ?: failure(
        RequestErrors.Common.DataFormatMismatch(
            name = "cpid",
            expectedFormat = Cpid.pattern,
            actualValue = cpid
        )
    )

fun parseSingleStageOcid(ocid: String): Result<Ocid, RequestErrors> = Ocid.SingleStage.tryCreateOrNull(ocid)
    ?.asSuccess()
    ?: failure(
        RequestErrors.Common.DataFormatMismatch(
            name = "ocid",
            expectedFormat = Ocid.SingleStage.pattern,
            actualValue = ocid
        )
    )

