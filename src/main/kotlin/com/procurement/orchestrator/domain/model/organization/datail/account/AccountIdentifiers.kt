package com.procurement.orchestrator.domain.model.organization.datail.account

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class AccountIdentifiers(
    values: List<AccountIdentifier> = emptyList()
) : List<AccountIdentifier> by values,
    IdentifiableObjects<AccountIdentifier, AccountIdentifiers>,
    Serializable {

    constructor(value: AccountIdentifier) : this(listOf(value))

    override operator fun plus(other: AccountIdentifiers) =
        AccountIdentifiers(this as List<AccountIdentifier> + other as List<AccountIdentifier>)

    override operator fun plus(others: List<AccountIdentifier>) =
        AccountIdentifiers(this as List<AccountIdentifier> + others)

    override fun updateBy(src: AccountIdentifiers) = AccountIdentifiers(update(dst = this, src = src))
}


