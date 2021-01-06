package com.procurement.orchestrator.domain.model.tender.criteria.requirement.eligible.evidence

import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.document.DocumentReference
import com.procurement.orchestrator.domain.model.or
import java.io.Serializable

class EligibleEvidence(
    val id: String,
    val title: String,
    val description: String?,
    val type: EligibleEvidenceType,
    val relatedDocument: DocumentReference?
) : IdentifiableObject<EligibleEvidence>, Serializable {

    override fun updateBy(src: EligibleEvidence) = EligibleEvidence(
        id = id,
        title = src.title,
        description = src.description or description,
        type = src.type,
        relatedDocument = src.relatedDocument or relatedDocument
    )
}
