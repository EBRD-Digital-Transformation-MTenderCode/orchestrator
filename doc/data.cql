CREATE KEYSPACE IF NOT EXISTS ocds WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

CREATE TABLE IF NOT EXISTS  ocds.orchestrator_operation_step (
    process_id text,
    task_id text,
    step_date timestamp,
    json_data text,
    json_params text,
    primary key(process_id, task_id));

CREATE TABLE IF NOT EXISTS  ocds.orchestrator_request (
    request_id text,
    request_date timestamp,
    operation_id text,
    json_data text,
    json_params text,
    primary key(request_id, request_date)
)WITH CLUSTERING ORDER BY (request_date DESC);

CREATE TABLE IF NOT EXISTS  ocds.orchestrator_operation (
    operation_id text,
    process_id text,
    primary key(operation_id));


CREATE TABLE IF NOT EXISTS  ocds.orchestrator_stage (
    cp_id text,
    stage text,
    country text,
    pmd text,
    phase text,
    primary key(cp_id));

CREATE TABLE IF NOT EXISTS  ocds.orchestrator_rules (
    new_stage text,
    stage text,
    country text,
    pmd text,
    phase text,
    operation_type text,
    primary key(new_stage, stage, country, pmd, phase, operation_type));


SELECT * FROM ocds.orchestrator_operation_step;
SELECT * FROM ocds.orchestrator_operation;
SELECT * FROM ocds.orchestrator_request;

TRUNCATE ocds.orchestrator_operation_step;
TRUNCATE ocds.orchestrator_operation;
TRUNCATE ocds.orchestrator_request;