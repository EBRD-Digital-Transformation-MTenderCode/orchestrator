package com.procurement.orchestrator.cassandra;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OperationValue {
    private String transactionId;
    private Integer step;
    private String description;
    private String dataProducer;
    private String dataConsumer;
    private String processType;
    private String jsonData;
}
