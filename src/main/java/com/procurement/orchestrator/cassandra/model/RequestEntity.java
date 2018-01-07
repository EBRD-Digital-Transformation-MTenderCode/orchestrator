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
public class RequestEntity {

    private String txId;

    private Date requestDate;

    private String jsonData;

    private String jsonParams;

}
