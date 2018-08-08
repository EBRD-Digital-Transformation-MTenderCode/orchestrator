package com.procurement.orchestrator.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Rule {

    private String country;

    private String pmd;

    private String processType;

    private String prevStage;

    private String newStage;

    private String prevPhase;

    private String newPhase;

    private String operationType;
}