package com.procurement.orchestrator.domain.model.contract.confirmation.request

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Requests(values: List<Request> = emptyList()) : List<Request> by values,
                                                      IdentifiableObjects<Request, Requests>,
                                                      Serializable {

    override fun updateBy(src: Requests) = Requests(update(dst = this, src = src))
}
