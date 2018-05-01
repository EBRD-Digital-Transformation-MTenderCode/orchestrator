package com.procurement.orchestrator.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StageRulesEntity {

    private String country;

    private String pmd;

    private String operationType;

    private String stage;

}
