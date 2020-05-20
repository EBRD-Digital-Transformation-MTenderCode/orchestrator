package com.procurement.orchestrator.domain.extension.date

import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

private const val FORMAT_PATTERN = "uuuu-MM-dd'T'HH:mm:ss'Z'"
private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(FORMAT_PATTERN)
    .withResolverStyle(ResolverStyle.STRICT)

fun LocalDateTime.format(): String = this.format(formatter)

fun String.parseLocalDateTime(): LocalDateTime = LocalDateTime.parse(this, formatter)

fun String.tryParseLocalDateTime(): Result<LocalDateTime, String> = try {
    success(this.parseLocalDateTime())
} catch (ignore: Exception) {
    failure(FORMAT_PATTERN)
}

fun nowDefaultUTC(): LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)

fun LocalDateTime.convertToUTC(currentZone: ZoneId = ZoneId.systemDefault()): LocalDateTime =
    this.atZone(currentZone).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()

fun LocalDateTime.toMilliseconds(): Long = this.atZone(ZoneOffset.systemDefault())
    .toInstant()
    .toEpochMilli()
