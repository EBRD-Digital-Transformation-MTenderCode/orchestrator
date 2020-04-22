package com.procurement.orchestrator.domain.model.document

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update

class Documents(values: List<Document> = emptyList()) : List<Document> by values,
    IdentifiableObjects<Document, Documents> {

    override fun updateBy(src: Documents) = Documents(update(dst = this, src = src))
}
