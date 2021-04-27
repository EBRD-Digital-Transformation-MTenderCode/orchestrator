package com.procurement.orchestrator.domain.model.process.master.data

import com.procurement.orchestrator.domain.model.ComplexObjects
import java.io.Serializable

class Candidates(values: List<Candidate> = emptyList()) : List<Candidate> by values,
    ComplexObjects<Candidate, Candidates>,
    Serializable {

    constructor(value: Candidate) : this(listOf(value))

    override operator fun plus(other: Candidates) = Candidates(this as List<Candidate> + other as List<Candidate>)

    override operator fun plus(others: List<Candidate>) = Candidates(this as List<Candidate> + others)

    override fun combineBy(src: Candidates) = Candidates(ComplexObjects.merge(dst = this, src = src))
}
