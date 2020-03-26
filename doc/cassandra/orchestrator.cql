CREATE KEYSPACE orchestrator WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor' : 1};

CREATE TABLE IF NOT EXISTS orchestrator.steps (
    cpid         TEXT,
    operation_id TEXT,
    process_id   TEXT,
    task_id      TEXT,
    step_date    TIMESTAMP,
    request      TEXT,
    response     TEXT,
    context      TEXT,
    PRIMARY KEY (cpid, operation_id, process_id, task_id, step_date)
);

CREATE TABLE IF NOT EXISTS orchestrator.notice_queue (
    operation_id TEXT,
    timestamp    TIMESTAMP,
    cpid         TEXT,
    ocid         TEXT,
    action_name  TEXT,
    data         TEXT,
    PRIMARY KEY (operation_id, timestamp, cpid, ocid, action_name)
);

CREATE TABLE IF NOT EXISTS orchestrator.process_definitions (
    country                TEXT,
    pmd                    TEXT,
    process_name           TEXT,
    process_definition_key TEXT,
    PRIMARY KEY ((country, pmd, process_name))
);

CREATE TABLE IF NOT EXISTS orchestrator.rules (
    country                TEXT,
    pmd                    TEXT,
    process_definition_key TEXT,
    stage_from             TEXT,
    phase_from             TEXT,
    stage_to               TEXT,
    phase_to               TEXT,
    operation_type         TEXT,
    PRIMARY KEY ((country, pmd, process_definition_key), stage_from, phase_from)
);

CREATE TABLE IF NOT EXISTS orchestrator.requests (
    operation_id TEXT,
    timestamp    TIMESTAMP,
    request_id   TEXT,
    platform_id  TEXT,
    context      TEXT,
    payload      TEXT,
    PRIMARY KEY (operation_id, timestamp, request_id)
);

CREATE TABLE IF NOT EXISTS orchestrator.launched_processes (
    operation_id TEXT,
    timestamp    TIMESTAMP,
    process_id   TEXT,
    cpid         TEXT,
    PRIMARY KEY (operation_id)
);

CREATE TABLE IF NOT EXISTS orchestrator.process_context (
    cpid         TEXT,
    timestamp    TIMESTAMP,
    operation_id TEXT,
    context      TEXT,
    PRIMARY KEY (cpid, timestamp, operation_id)
) WITH CLUSTERING ORDER BY (timestamp DESC);

CREATE TABLE IF NOT EXISTS orchestrator.error_descriptions (
    code        TEXT,
    language    TEXT,
    description TEXT,
    PRIMARY KEY (code, language)
);
