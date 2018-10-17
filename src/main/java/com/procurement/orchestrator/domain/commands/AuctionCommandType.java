package com.procurement.orchestrator.domain.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum AuctionCommandType {

    VALIDATE("validateAuctionDetails"),
    SCHEDULE("scheduleAuctions"),
    START("auctionsStart"),
    END("auctionsEnd"),
    CANCEL("auctionsCancellation");

    private static final Map<String, AuctionCommandType> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final AuctionCommandType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    AuctionCommandType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static AuctionCommandType fromValue(final String value) {
        final AuctionCommandType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(AuctionCommandType.class.getName(), value, Arrays.toString(values()));
        }
        return constant;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }
}
