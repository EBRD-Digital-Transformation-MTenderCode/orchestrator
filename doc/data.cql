CREATE KEYSPACE IF NOT EXISTS ocds WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

CREATE TABLE IF NOT EXISTS  ocds.orchestrator_operation (
    transaction_id text,
    operation_step int,
    operation_date timestamp,
    operation_desc text,
    data_producer text,
    data_consumer text,
    process_type text,
    json_data text,
    primary key(transaction_id, operation_step, operation_date)
);


SELECT * FROM ocds.orchestrator_operation;

TRUNCATE ocds.orchestrator_operation;