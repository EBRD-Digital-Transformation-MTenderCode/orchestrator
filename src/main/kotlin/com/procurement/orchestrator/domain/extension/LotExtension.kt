package com.procurement.orchestrator.domain.extension

import com.procurement.orchestrator.domain.model.lot.Lot

fun List<Lot>.merge(values: List<Lot>): List<Lot> = this + values
