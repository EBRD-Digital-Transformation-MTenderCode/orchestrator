package com.procurement.orchestrator.domain.extension

import com.procurement.orchestrator.domain.model.amendment.AmendmentId
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.tender.Tender

fun Tender.lotIds(): Set<LotId> = lots.asSequence()
    .map { lot -> lot.id }
    .toSet()

fun Tender.amendmentIds(): Set<AmendmentId> = amendments.asSequence()
    .map { amendment -> amendment.id }
    .toSet()
