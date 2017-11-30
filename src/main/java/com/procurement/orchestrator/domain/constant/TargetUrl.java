package com.procurement.orchestrator.domain.constant;

public enum TargetUrl {

    ACCESS("http://localhost:8080/saveEIN"),
    NOTICE("/notice");

    private final String value;

    TargetUrl(final String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
