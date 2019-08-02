package com.procurement.orchestrator.exception;

import lombok.Getter;

@Getter
public class AddressEnricherException extends RuntimeException {
    private String code = "400.00.00.00";

    public AddressEnricherException(String description) {
        super(description);
    }
}
