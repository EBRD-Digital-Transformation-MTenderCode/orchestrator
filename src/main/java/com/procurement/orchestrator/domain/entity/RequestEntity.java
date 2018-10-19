package com.procurement.orchestrator.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestEntity {

    private String requestId;

    private Date requestDate;

    private String operationId;

    private String jsonData;

    private String context;

    private Long writeTime;
}
