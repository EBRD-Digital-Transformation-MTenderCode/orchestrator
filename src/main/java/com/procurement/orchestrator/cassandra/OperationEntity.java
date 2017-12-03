package com.procurement.orchestrator.cassandra;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Setter
@Table("orchestrator_operation")
public class OperationEntity {

    @PrimaryKeyColumn(name = "transaction_id", type = PrimaryKeyType.PARTITIONED)
    private String transactionId;

    @PrimaryKeyColumn(name = "operation_step", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private Integer step;

    @PrimaryKeyColumn(name = "operation_date", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private LocalDateTime date;

    @Column(value = "operation_desc")
    private String description;

    @Column(value = "data_producer")
    private String dataProducer;

    @Column(value = "data_consumer")
    private String dataConsumer;

    @Column(value = "process_type")
    private String processType;

    @Column(value = "json_data")
    private String jsonData;
}
