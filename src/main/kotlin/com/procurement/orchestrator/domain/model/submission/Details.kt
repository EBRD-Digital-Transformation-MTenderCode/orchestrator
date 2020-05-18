package com.procurement.orchestrator.domain.model.submission

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Details(values: List<Submission> = emptyList()) : List<Submission> by values,
                                                        IdentifiableObjects<Submission, Details>,
                                                        Serializable {

    constructor(submission: Submission) : this(listOf(submission))

    override operator fun plus(other: Details): Details =
        Details(this as List<Submission> + other as List<Submission>)

    override operator fun plus(others: List<Submission>): Details = Details(this as List<Submission> + others)

    override fun updateBy(src: Details) = Details(update(dst = this, src = src))
}
