package com.procurement.orchestrator.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;

@Component
public class DateUtil {

    public LocalDateTime getNowUTC() {
        return LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    }

    public long getMilliUTC(final LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.UTC)
                            .toEpochMilli();
    }
}
