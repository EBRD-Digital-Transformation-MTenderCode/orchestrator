package com.procurement.orchestrator.domain.model.person

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Persons(values: List<Person> = emptyList()) : List<Person> by values,
                                                    IdentifiableObjects<Person, Persons>,
                                                    Serializable {

    override fun updateBy(src: Persons) = Persons(update(dst = this, src = src))
}
