package com.procurement.orchestrator.domain;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OcidMatcher {
    private final static String STAGES = Arrays.stream(Stage.values())
            .map(stage -> stage.value().toUpperCase())
            .collect(Collectors.joining("|", "(", ")"));

    public final static String REGEX = "^[a-z]{4}-[a-z0-9]{6}-[A-Z]{2}-[0-9]{13}-" + STAGES + "-[0-9]{13}$";

    private final static Pattern PATTERN = Pattern.compile(REGEX);

    public static Boolean isOcid(String value){
        return PATTERN.matcher(value).matches();
    }
}
