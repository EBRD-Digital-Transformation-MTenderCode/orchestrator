package com.procurement.orchestrator.domain.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import com.procurement.orchestrator.exception.EnumException;

public enum AwardStatusDetails {
    ACTIVE("active"),
    AWAITING("awaiting"),
    CONSIDERATION("consideration"),
    EMPTY("empty"),
    LACK_OF_QUALIFICATIONS("lackOfQualifications"),
    LACK_OF_SUBMISSIONS("lackOfSubmissions"),
    LOT_CANCELLED("lotCancelled"),
    NO_OFFERS_RECEIVED("noOffersReceived"),
    PENDING("pending"),
    UNSUCCESSFUL("unsuccessful");

    private String value;

    private static Map<String, AwardStatusDetails> elements = Arrays.stream(values())
        .collect(Collectors.toMap(
            item -> item.value.toUpperCase(),
            item -> item)
        );

    AwardStatusDetails(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static AwardStatusDetails fromString(String value) {
        final AwardStatusDetails element = elements.get(value.toUpperCase());
        if (element != null) {
            return element;
        } else {
            throw new EnumException(AwardStatusDetails.class.getCanonicalName(), value, Arrays.toString(values()));
        }

    }
}
