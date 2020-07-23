package com.procurement.orchestrator.domain.model.invitation

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Invitations(values: List<Invitation> = emptyList()) : List<Invitation> by values,
    IdentifiableObjects<Invitation, Invitations>,
    Serializable {

    constructor(value: Invitation) : this(listOf(value))

    override operator fun plus(other: Invitations) =
        Invitations(this as List<Invitation> + other as List<Invitation>)

    override operator fun plus(others: List<Invitation>) =
        Invitations(this as List<Invitation> + others)

    override fun updateBy(src: Invitations) = Invitations(update(dst = this, src = src))
}
