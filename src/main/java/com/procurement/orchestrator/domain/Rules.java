package com.procurement.orchestrator.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Rules {

    private String newStage;

    private String stage;

    private String country;

    private String pmd;

    private String phase;

    private String operationType;

}
