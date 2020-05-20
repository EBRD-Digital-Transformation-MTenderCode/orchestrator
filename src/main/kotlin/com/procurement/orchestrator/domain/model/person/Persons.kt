package com.procurement.orchestrator.domain.model.person

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Persons(values: List<Person> = emptyList()) : List<Person> by values,
                                                    IdentifiableObjects<Person, Persons>,
                                                    Serializable {

    constructor(value: Person) : this(listOf(value))

    override operator fun plus(other: Persons) = Persons(this as List<Person> + other as List<Person>)

    override operator fun plus(others: List<Person>) = Persons(this as List<Person> + others)

    override fun updateBy(src: Persons) = Persons(update(dst = this, src = src))
}
