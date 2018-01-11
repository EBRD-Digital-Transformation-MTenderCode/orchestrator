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
public class RequestEntity {

    private String txId;

    private Date requestDate;

    private String jsonData;

    private String jsonParams;

}
