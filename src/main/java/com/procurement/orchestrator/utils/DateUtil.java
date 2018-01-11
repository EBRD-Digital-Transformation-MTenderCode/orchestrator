package com.procurement.orchestrator.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class DateUtil {

    private final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral('T')
            .append(DateTimeFormatter.ISO_LOCAL_TIME)
            .appendLiteral('Z')
            .toFormatter();

    public Date getNowUTC() {
        return localDateTimeToDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
    }

    public LocalDateTime localDateTimeNowUTC() {
        return LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    }

    public long getMilliUTC(final LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.UTC)
                .toEpochMilli();
    }

    public LocalDateTime stringToLocal(String dateTime) {
        return LocalDateTime.parse(dateTime, FORMATTER);
    }

    public String format(final LocalDateTime localDateTime) {
        return localDateTime.format(FORMATTER);
    }

    public LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
    }

    public Date localDateTimeToDate(LocalDateTime startDate) {
        return Date.from(startDate.toInstant(ZoneOffset.UTC));
    }

}
