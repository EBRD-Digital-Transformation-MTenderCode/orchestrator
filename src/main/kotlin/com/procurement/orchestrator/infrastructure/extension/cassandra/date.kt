package com.procurement.orchestrator.infrastructure.extension.cassandra

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

fun Date.toLocalDateTime(zoneId: ZoneId = ZoneOffset.UTC): LocalDateTime = this.toInstant()
    .atZone(zoneId)
    .toLocalDateTime()

fun LocalDateTime.toCassandraTimestamp(zoneId: ZoneId = ZoneOffset.UTC): Date =
    Date.from(this.atZone(zoneId).toInstant())
