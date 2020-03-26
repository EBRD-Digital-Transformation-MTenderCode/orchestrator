package com.procurement.orchestrator.domain.extension

import com.procurement.orchestrator.domain.model.amendment.Amendment

fun List<Amendment>.union(values: List<Amendment>): List<Amendment> = this + values
