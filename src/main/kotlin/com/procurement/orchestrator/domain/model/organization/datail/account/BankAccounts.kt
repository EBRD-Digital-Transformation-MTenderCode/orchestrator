package com.procurement.orchestrator.domain.model.organization.datail.account

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge

class BankAccounts(
    values: List<BankAccount> = emptyList()
) : List<BankAccount> by values, ComplexObjects<BankAccount, BankAccounts> {

    override fun combineBy(src: BankAccounts) = BankAccounts(merge(dst = this, src = src))
}
