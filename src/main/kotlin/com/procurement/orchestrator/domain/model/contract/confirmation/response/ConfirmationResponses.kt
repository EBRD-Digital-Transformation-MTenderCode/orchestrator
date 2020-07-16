package com.procurement.orchestrator.domain.model.contract.confirmation.response

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class ConfirmationResponses(
    values: List<ConfirmationResponse> = emptyList()
) : List<ConfirmationResponse> by values,
    IdentifiableObjects<ConfirmationResponse, ConfirmationResponses>,
    Serializable {

    constructor(value: ConfirmationResponse) : this(listOf(value))

    override operator fun plus(other: ConfirmationResponses) =
        ConfirmationResponses(this as List<ConfirmationResponse> + other as List<ConfirmationResponse>)

    override operator fun plus(others: List<ConfirmationResponse>) =
        ConfirmationResponses(this as List<ConfirmationResponse> + others)

    override fun updateBy(src: ConfirmationResponses) = ConfirmationResponses(update(dst = this, src = src))
}
