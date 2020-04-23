package com.procurement.orchestrator.domain.model.organization.datail.account

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class AccountIdentifiers(
    values: List<AccountIdentifier> = emptyList()
) : List<AccountIdentifier> by values,
    IdentifiableObjects<AccountIdentifier, AccountIdentifiers>,
    Serializable {

    override fun updateBy(src: AccountIdentifiers) = AccountIdentifiers(update(dst = this, src = src))
}


