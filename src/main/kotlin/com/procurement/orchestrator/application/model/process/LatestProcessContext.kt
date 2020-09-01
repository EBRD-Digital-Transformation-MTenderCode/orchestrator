package com.procurement.orchestrator.application.model.process

import com.procurement.orchestrator.application.model.Phase
import com.procurement.orchestrator.application.model.Stage
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.ProcurementMethodDetails
import com.procurement.orchestrator.domain.model.tender.AwardCriteria

class LatestProcessContext(
    val cpid: Cpid,
    val ocid: Ocid,
    val stage: Stage,
    val phase: Phase,
    val country: String,
    val language: String,
    val pmd: ProcurementMethodDetails,
    val isAuction: Boolean,
    val mainProcurementCategory: String? = null,
    val awardCriteria: AwardCriteria? = null,
    val relatedProcess: RelatedProcess? = null
) {
    data class RelatedProcess(
        val cpid: Cpid?,
        val ocid: Ocid?
    )
}

