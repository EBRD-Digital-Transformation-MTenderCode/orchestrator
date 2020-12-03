package com.procurement.orchestrator.application.model.context.extension

import com.procurement.orchestrator.application.model.context.GlobalContext
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.award.Award
import com.procurement.orchestrator.domain.model.award.AwardId
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.organization.Organization

private const val NAME = "awards"

fun GlobalContext.getAwardIfOnlyOne(): Result<Award, Fail.Incident.Bpms.Context> =
    this.awards.getElementIfOnlyOne(name = NAME)

fun GlobalContext.getAwardsIfNotEmpty(): Result<List<Award>, Fail.Incident.Bpms.Context> =
    this.awards.getIfNotEmpty(name = NAME)

fun GlobalContext.findAwardById(id: AwardId): Result<Award, Fail.Incident.Bpms.Context.NotFoundElement> = this.awards
    .find { it.id == id }
    ?.asSuccess()
    ?: failure(Fail.Incident.Bpms.Context.NotFoundElement(id = id.toString(), name = NAME))

fun Award.getSuppliersIfNotEmpty(): Result<List<Organization>, Fail.Incident.Bpms.Context> =
    this.suppliers.getIfNotEmpty(name = "$NAME.suppliers")

fun Award.getDocumentsIfNotEmpty(): Result<List<Document>, Fail.Incident.Bpms.Context> =
    this.documents.getIfNotEmpty(name = "$NAME.documents")
