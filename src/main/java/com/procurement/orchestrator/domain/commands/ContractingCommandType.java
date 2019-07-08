package com.procurement.orchestrator.domain.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum ContractingCommandType {

    CHECK_CAN("checkCan"),
    CHECK_CAN_BY_AWARD("checkCanBiAwardId"),
    CREATE_CAN("createCan"),
    GET_CANS("getCans"),
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
    TREASURY_RESPONSE_PROCESSING("treasuryResponseProcessing"),
    ACTIVATION_AC("activationAC"),
    UPDATE_CAN_DOCS("updateCanDocs"),
    CANCEL_CAN("cancelCan"),
    CONFIRMATION_CAN("confirmationCan");

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
