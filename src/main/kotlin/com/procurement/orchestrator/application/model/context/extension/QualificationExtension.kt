package com.procurement.orchestrator.application.model.context.extension

import com.procurement.orchestrator.application.model.context.GlobalContext
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.qualification.Qualification

private const val NAME = "qualifications"

fun GlobalContext.getQualificationIfOnlyOne(): Result<Qualification, Fail.Incident.Bpms.Context> =
    this.qualifications.getElementIfOnlyOne(name = NAME)