package com.procurement.orchestrator.domain.enums;

import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum AwardStatusDetails {
    PENDING("pending"),
    ACTIVE("active"),
    UNSUCCESSFUL("unsuccessful"),
    CONSIDERATION("consideration"),
    EMPTY("empty"),
    AWAITING("awaiting"),
    NO_OFFERS_RECEIVED("noOffersReceived"),
    LOT_CANCELLED("lotCancelled");

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
