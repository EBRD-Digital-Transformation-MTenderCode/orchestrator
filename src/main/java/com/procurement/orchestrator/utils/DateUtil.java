package com.procurement.orchestrator.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import org.springframework.stereotype.Component;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.temporal.ChronoField.*;

@Component
public class DateUtil {

    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE)
            .appendLiteral('T')
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .appendLiteral('Z')
            .toFormatter();

    public Date dateNowUTC() {
        return localDateTimeToDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
    }

    public LocalDateTime localDateTimeNowUTC() {
        return LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    }

    public LocalDateTime stringToLocal(final String dateTime) {
        return LocalDateTime.parse(dateTime, FORMATTER);
    }

    public String format(final LocalDateTime localDateTime) {
        return localDateTime.format(FORMATTER);
    }

    public Date localDateTimeToDate(final LocalDateTime startDate) {
        return Date.from(startDate.toInstant(ZoneOffset.UTC));
    }

}
