package com.procurement.orchestrator.cassandra.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
