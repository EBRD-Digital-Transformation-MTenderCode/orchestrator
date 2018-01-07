CREATE KEYSPACE IF NOT EXISTS ocds WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

CREATE TABLE IF NOT EXISTS  ocds.orchestrator_operation (
    tx_id text,
    operation_date timestamp,
    process_id text,
    task_id text,
    json_data text,
    json_params text,
    request_date timestamp,
    primary key(tx_id, operation_date)
)WITH CLUSTERING ORDER BY (operation_date DESC);

SELECT * FROM ocds.orchestrator_operation;

TRUNCATE ocds.orchestrator_operation;


CREATE TABLE IF NOT EXISTS  ocds.orchestrator_request (
    tx_id text,
    request_date timestamp,
    json_data text,
    json_params text,
    primary key(tx_id, request_date)
)WITH CLUSTERING ORDER BY (request_date DESC);

SELECT * FROM ocds.orchestrator_request;

TRUNCATE ocds.orchestrator_request;