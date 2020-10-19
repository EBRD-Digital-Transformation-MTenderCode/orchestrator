package com.procurement.orchestrator.application.model.context.extension

import com.procurement.orchestrator.application.model.context.GlobalContext
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.amendment.Amendment
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.enquiry.Enquiry
import com.procurement.orchestrator.domain.model.item.Item
import com.procurement.orchestrator.domain.model.lot.Lot
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.domain.model.tender.target.Target

private const val PATH = "tender"

fun GlobalContext.tryGetTender(): Result<Tender, Fail.Incident.Bpms.Context> =
    this.tender
        ?.asSuccess()
        ?: failure(Fail.Incident.Bpms.Context.Missing(name = "tender"))

fun Tender.getAmendmentIfOnlyOne(): Result<Amendment, Fail.Incident.Bpms.Context> =
    this.amendments.getElementIfOnlyOne(name = "amendments", path = PATH)

fun Tender.getAmendmentsIfNotEmpty(): Result<List<Amendment>, Fail.Incident.Bpms.Context> =
    this.amendments.getIfNotEmpty(name = "amendments", path = PATH)

fun Tender.getLotIfOnlyOne(): Result<Lot, Fail.Incident.Bpms.Context> =
    this.lots.getElementIfOnlyOne(name = "lots", path = PATH)

fun Tender.getLotsIfNotEmpty(): Result<List<Lot>, Fail.Incident.Bpms.Context> =
    this.lots.getIfNotEmpty(name = "lots", path = PATH)

fun Tender.getTargetsIfNotEmpty(): Result<List<Target>, Fail.Incident.Bpms.Context> =
    this.targets.getIfNotEmpty(name = "targets", path = PATH)

fun Tender.getItemsIfNotEmpty(): Result<List<Item>, Fail.Incident.Bpms.Context> =
    this.items.getIfNotEmpty(name = "items", path = PATH)

fun Tender.getDocumentsIfNotEmpty(): Result<List<Document>, Fail.Incident.Bpms.Context> =
    this.documents.getIfNotEmpty(name = "documents", path = PATH)

fun Tender.getEnquiriesIfNotEmpty(): Result<List<Enquiry>, Fail.Incident.Bpms.Context> =
    this.enquiries.getIfNotEmpty(name = "enquiries", path = PATH)
