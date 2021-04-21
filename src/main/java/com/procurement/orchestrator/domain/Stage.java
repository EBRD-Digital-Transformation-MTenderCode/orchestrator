package com.procurement.orchestrator.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;
import com.procurement.orchestrator.exception.OperationException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Stage {
    AC("AC"),
    AP("AP"),
    EI("EI"),
    EV("EV"),
    FE("FE"),
    FS("FS"),
    NP("NP"),
    PC("PC"),
    PIN("PIN"),
    PN("PN"),
    PQ("PQ"),
    PS("PS"),
    RQ("RQ"),
    TP("TP");

    private static final Map<String, Stage> CONSTANTS = new HashMap<>();
    private final String value;
    private static final int STAGE_POSITION = 4;

    static {
        for (final Stage c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    Stage(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static Stage fromValue(final String value) {
        final Stage constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(Stage.class.getName(), value, Arrays.toString(values()));
        }
        return constant;
    }

    public static Stage fromOcid(final String ocid) {
        final String[] ocidParts = ocid.split("-");

        if (STAGE_POSITION >= ocid.length())
            throw new OperationException("Invalid ocid. Could not extract stage from ocid.");

        Stage result = CONSTANTS.get(ocidParts[STAGE_POSITION]);
        if (result == null)
            throw new OperationException("Invalid ocid. Could not extract stage from ocid.");
        else return result;
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
