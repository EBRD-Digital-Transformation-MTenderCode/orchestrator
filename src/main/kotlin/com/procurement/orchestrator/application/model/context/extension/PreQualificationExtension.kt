package com.procurement.orchestrator.application.model.context.extension

import com.procurement.orchestrator.application.model.context.GlobalContext
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.qualification.PreQualification

private const val PREQUALIFICATION_PATH = "qualifications"

fun GlobalContext.tryGetPreQualification(): Result<PreQualification, Fail.Incident.Bpms.Context> =
    this.preQualification
        ?.asSuccess()
        ?: Result.failure(Fail.Incident.Bpms.Context.Missing(name = "preQualification"))

fun PreQualification.tryGetPeriod(): Result<Period, Fail.Incident.Bpms.Context> =
    this.period
        ?.asSuccess()
        ?: Result.failure(Fail.Incident.Bpms.Context.Missing(name = "period", path = "${PREQUALIFICATION_PATH}.period"))
