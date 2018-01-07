package com.procurement.orchestrator.cassandra.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OperationEntity {

    private String txId;

    private Date date;

    private String processId;

    private String taskId;

    private String jsonData;

    private String jsonParams;

    private Date requestDate;
}
