package com.procurement.orchestrator.domain.model.document

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Documents(values: List<Document> = emptyList()) : List<Document> by values,
                                                        IdentifiableObjects<Document, Documents>,
                                                        Serializable {

    override fun updateBy(src: Documents) = Documents(update(dst = this, src = src))
}
