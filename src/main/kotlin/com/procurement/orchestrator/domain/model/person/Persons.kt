package com.procurement.orchestrator.domain.model.person

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update

class Persons(values: List<Person> = emptyList()) : List<Person> by values, IdentifiableObjects<Person, Persons> {

    override fun updateBy(src: Persons) = Persons(update(dst = this, src = src))
}
