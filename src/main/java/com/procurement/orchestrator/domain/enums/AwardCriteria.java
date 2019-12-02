package com.procurement.orchestrator.domain.enums;

import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum AwardCriteria {
    COST_ONLY("costOnly"),
    QUALITY_ONLY("qualityOnly"),
    RATED_CRITERIA("ratedCriteria"),
    PRICE_ONLY("priceOnly");

    private String value;

    private static Map<String, AwardCriteria> elements = Arrays.stream(values())
            .collect(Collectors.toMap(
                    item -> item.value.toUpperCase(),
                    item -> item)
            );

    AwardCriteria(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static AwardCriteria fromString(String value) {
        final AwardCriteria element = elements.get(value.toUpperCase());
        if (element != null) {
            return element;
        } else {
            throw new EnumException(AwardCriteria.class.getCanonicalName(), value, Arrays.toString(values()));
        }

    }
}
