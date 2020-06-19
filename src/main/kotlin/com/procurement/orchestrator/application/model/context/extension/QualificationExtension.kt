package com.procurement.orchestrator.application.model.context.extension

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.qualification.Qualification
import com.procurement.orchestrator.domain.model.qualification.Qualifications

private const val NAME_SUBMISSIONS = "qualifications"

fun Qualifications.getQualificationIfOnlyOne(): Result<Qualification, Fail.Incident.Bpms.Context> =
    this.getElementIfOnlyOne(name = NAME_SUBMISSIONS)

