package com.procurement.orchestrator.domain.model.document

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Documents(values: List<Document> = emptyList()) : List<Document> by values,
                                                        IdentifiableObjects<Document, Documents>,
                                                        Serializable {

    constructor(value: Document) : this(listOf(value))

    override operator fun plus(other: Documents) = Documents(this as List<Document> + other as List<Document>)

    override operator fun plus(others: List<Document>) = Documents(this as List<Document> + others)

    override fun updateBy(src: Documents) = Documents(update(dst = this, src = src))
}
