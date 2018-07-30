CREATE KEYSPACE IF NOT EXISTS ocds WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

CREATE TABLE IF NOT EXISTS  ocds.orchestrator_operation_step (
    process_id text,
    task_id text,
    operation_id text,
    step_date timestamp,
    request_data text,
    response_data text,
    context text,
    cp_id text,
    primary key(process_id, task_id));

CREATE TABLE IF NOT EXISTS  ocds.orchestrator_request (
    request_id text,
    request_date timestamp,
    operation_id text,
    json_data text,
    context text,
    primary key(request_id, request_date)
)WITH CLUSTERING ORDER BY (request_date DESC);

CREATE TABLE IF NOT EXISTS  ocds.orchestrator_operation (
    operation_id text,
    process_id text,
    primary key(operation_id));

CREATE TABLE IF NOT EXISTS  ocds.orchestrator_context (
    cp_id text,
    context text,
    primary key(cp_id));

CREATE TABLE IF NOT EXISTS  ocds.orchestrator_stage (
    country text,
    pmd text,
    operation_type text,
    stage text,
    primary key(country, pmd, operation_type));

CREATE TABLE IF NOT EXISTS  ocds.orchestrator_rules (
    new_stage text,
    prev_stage text,
    country text,
    pmd text,
    phase text,
    operation_type text,
    primary key(new_stage, prev_stage, country, pmd, phase));

INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('PS', 'PS', 'MD', 'RT', 'AWARDPERIOD', 'awardByBid');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('PQ', 'PQ', 'MD', 'RT', 'AWARDPERIOD', 'awardByBid');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('EV', 'EV', 'MD', 'RT', 'AWARDPERIOD', 'awardByBidEv');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('PQ', 'PS', 'MD', 'RT', 'ENDSTAGE', 'startNewStage');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('EV', 'PQ', 'MD', 'RT', 'ENDSTAGE', 'startNewStage');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('PIN', 'PN', 'MD', 'RT', 'PLANNING', 'updatePIN');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('PS', 'PN', 'MD', 'RT', 'PLANNING', 'updateCN');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('PS', 'PIN', 'MD', 'RT', 'PLANNED', 'updateCN');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('EV', 'PN', 'MD', 'OT', 'PLANNING', 'updateCN');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('EV', 'PIN', 'MD', 'OT', 'PLANNED', 'updateCN');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('PS', 'PS', 'MD', 'RT', 'AWARDPERIOD', 'awardPeriodEnd');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('PQ', 'PQ', 'MD', 'RT', 'AWARDPERIOD', 'awardPeriodEnd');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('EV', 'EV', 'MD', 'RT', 'AWARDPERIOD', 'awardPeriodEndEv');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('EV', 'EV', 'MD', 'OT', 'AWARDPERIOD', 'awardByBidEv');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('EV', 'EV', 'MD', 'OT', 'AWARDPERIOD', 'awardPeriodEndEv');

INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('PS', 'PS', 'MD', 'TEST_RT', 'AWARDPERIOD', 'awardByBid');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('PQ', 'PQ', 'MD', 'TEST_RT', 'AWARDPERIOD', 'awardByBid');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('EV', 'EV', 'MD', 'TEST_RT', 'AWARDPERIOD', 'awardByBidEv');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('PQ', 'PS', 'MD', 'TEST_RT', 'ENDSTAGE', 'startNewStage');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('EV', 'PQ', 'MD', 'TEST_RT', 'ENDSTAGE', 'startNewStage');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('PIN', 'PN', 'MD', 'TEST_RT', 'PLANNING', 'updatePIN');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('PS', 'PN', 'MD', 'TEST_RT', 'PLANNING', 'updateCN');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('PS', 'PIN', 'MD', 'TEST_RT', 'PLANNED', 'updateCN');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('EV', 'PN', 'MD', 'TEST_OT', 'PLANNING', 'updateCN');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('EV', 'PIN', 'MD', 'TEST_OT', 'PLANNED', 'updateCN');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('PS', 'PS', 'MD', 'TEST_RT', 'AWARDPERIOD', 'awardPeriodEnd');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('PQ', 'PQ', 'MD', 'TEST_RT', 'AWARDPERIOD', 'awardPeriodEnd');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('EV', 'EV', 'MD', 'TEST_RT', 'AWARDPERIOD', 'awardPeriodEndEv');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('EV', 'EV', 'MD', 'TEST_OT', 'AWARDPERIOD', 'awardByBidEv');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('EV', 'EV', 'MD', 'TEST_OT', 'AWARDPERIOD', 'awardPeriodEndEv');

INSERT INTO ocds.orchestrator_stage (country, pmd, operation_type, stage)  VALUES ('MD', 'RT', 'createCN', 'PS');
INSERT INTO ocds.orchestrator_stage (country, pmd, operation_type, stage)  VALUES ('MD', 'OT', 'createCN', 'EV');
INSERT INTO ocds.orchestrator_stage (country, pmd, operation_type, stage)  VALUES ('MD', 'TEST_RT', 'createCN', 'PS');
INSERT INTO ocds.orchestrator_stage (country, pmd, operation_type, stage)  VALUES ('MD', 'TEST_OT', 'createCN', 'EV');

INSERT INTO ocds.orchestrator_stage (country, pmd, operation_type, stage)  VALUES ('MD', 'RT', 'createPIN', 'PIN');
INSERT INTO ocds.orchestrator_stage (country, pmd, operation_type, stage)  VALUES ('MD', 'OT', 'createPIN', 'PIN');
INSERT INTO ocds.orchestrator_stage (country, pmd, operation_type, stage)  VALUES ('MD', 'TEST_RT', 'createPIN', 'PIN');
INSERT INTO ocds.orchestrator_stage (country, pmd, operation_type, stage)  VALUES ('MD', 'TEST_OT', 'createPIN', 'PIN');

INSERT INTO ocds.orchestrator_stage (country, pmd, operation_type, stage)  VALUES ('MD', 'RT', 'createPN', 'PN');
INSERT INTO ocds.orchestrator_stage (country, pmd, operation_type, stage)  VALUES ('MD', 'OT', 'createPN', 'PN');
INSERT INTO ocds.orchestrator_stage (country, pmd, operation_type, stage)  VALUES ('MD', 'TEST_RT', 'createPN', 'PN');
INSERT INTO ocds.orchestrator_stage (country, pmd, operation_type, stage)  VALUES ('MD', 'TEST_OT', 'createPN', 'PN');

SELECT * FROM ocds.orchestrator_operation_step;
SELECT * FROM ocds.orchestrator_operation_step where operation_id = '0e7e8f29-717b-486d-ba95-81abbcc70a42' allow filtering;
SELECT * FROM ocds.orchestrator_operation_step where cp_id = 'ocds-t1s2t3-TEST-1525704819499' allow filtering;
SELECT * FROM ocds.orchestrator_operation_step where cp_id = 'ocds-t1s2t3-TEST-1525365442353' and task_id = 'SubmissionCreateBidTask'  allow filtering;
SELECT * FROM ocds.orchestrator_operation_step where task_id = 'AccessCreatePnTask' allow filtering;

SELECT * FROM ocds.orchestrator_request where operation_id = 'b7eb0d2d-7bbb-4f71-9e06-3731f249a7e8' allow filtering;
SELECT * FROM ocds.orchestrator_stage where cp_id = 'ocds-t1s2t3-TEST-1524663516399';