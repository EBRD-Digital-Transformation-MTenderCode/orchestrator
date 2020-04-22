package com.procurement.orchestrator.application.model.context.members

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import com.procurement.orchestrator.domain.model.contract.Contract

import java.io.Serializable

class Contracts(values: List<Contract>) : List<Contract> by values, IdentifiableObjects<Contract, Contracts>,
    Serializable {

    constructor(contract: Contract) : this(listOf(contract))

    operator fun plus(other: Contracts): Contracts = Contracts(this as List<Contract> + other as List<Contract>)

    operator fun plus(others: List<Contract>): Contracts = Contracts(this as List<Contract> + others)

    override fun updateBy(src: Contracts) = Contracts(update(dst = this, src = src))
}
