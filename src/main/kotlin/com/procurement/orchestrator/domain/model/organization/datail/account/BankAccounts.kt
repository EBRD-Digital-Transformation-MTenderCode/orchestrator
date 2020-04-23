package com.procurement.orchestrator.domain.model.organization.datail.account

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge
import java.io.Serializable

class BankAccounts(values: List<BankAccount> = emptyList()) : List<BankAccount> by values,
                                                              ComplexObjects<BankAccount, BankAccounts>,
                                                              Serializable {

    constructor(value: BankAccount) : this(listOf(value))

    override operator fun plus(other: BankAccounts) =
        BankAccounts(this as List<BankAccount> + other as List<BankAccount>)

    override operator fun plus(others: List<BankAccount>) = BankAccounts(this as List<BankAccount> + others)

    override fun combineBy(src: BankAccounts) = BankAccounts(merge(dst = this, src = src))
}
