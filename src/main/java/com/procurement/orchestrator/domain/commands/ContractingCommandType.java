package com.procurement.orchestrator.domain.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum ContractingCommandType {

    CREATE_CAN("createCAN"),
    CREATE_AC("createAC"),
    UPDATE_AC("updateAC"),
    GET_BUDGET_SOURCES("getActualBudgetSources"),
    CHECK_STATUS_DETAILS("contractingCheckStatusDetails"),
    GET_RELATED_BID_ID("getRelatedBidId"),
    ISSUING_AC("issuingAC"),
    FINAL_UPDATE("finalUpdateAC"),
    BUYER_SIGNING_AC("buyerSigningAC"),
    SUPPLIER_SIGNING_AC("supplierSigningAC"),
    VERIFICATION_AC("verificationAC"),
    TREASURY_APPROVING_AC("treasuryApprovingAC"),
    ACTIVATION_AC("activationAC"),
    UPDATE_CAN_DOCS("addsCANDocs");

    private static final Map<String, ContractingCommandType> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final ContractingCommandType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    ContractingCommandType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static ContractingCommandType fromValue(final String value) {
        final ContractingCommandType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(ContractingCommandType.class.getName(), value, Arrays.toString(values()));
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
