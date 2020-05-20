package com.procurement.orchestrator.domain.model.contract.confirmation.request

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Requests(values: List<Request> = emptyList()) : List<Request> by values,
                                                      IdentifiableObjects<Request, Requests>,
                                                      Serializable {

    constructor(value: Request) : this(listOf(value))

    override operator fun plus(other: Requests) = Requests(this as List<Request> + other as List<Request>)

    override operator fun plus(others: List<Request>) = Requests(this as List<Request> + others)

    override fun updateBy(src: Requests) = Requests(update(dst = this, src = src))
}
