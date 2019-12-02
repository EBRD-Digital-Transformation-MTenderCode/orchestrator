package com.procurement.orchestrator.domain.enums;

import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum AwardCriteriaDetails {
    MANUAL("manual"),
    AUTOMATED("automated");

    private String value;

    private static Map<String, AwardCriteriaDetails> elements = Arrays.stream(values())
            .collect(Collectors.toMap(
                    item -> item.value.toUpperCase(),
                    item -> item)
            );

    AwardCriteriaDetails(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static AwardCriteriaDetails fromString(String value) {
        final AwardCriteriaDetails element = elements.get(value.toUpperCase());
        if (element != null) {
            return element;
        } else {
            throw new EnumException(AwardCriteriaDetails.class.getCanonicalName(), value, Arrays.toString(values()));
        }

    }
}
