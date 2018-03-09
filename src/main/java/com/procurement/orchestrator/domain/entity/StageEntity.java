package com.procurement.orchestrator.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StageEntity {

    private String cpId;

    private String stage;

    private String country;

    private String pmd;

}
