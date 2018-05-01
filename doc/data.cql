CREATE KEYSPACE IF NOT EXISTS ocds WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

CREATE TABLE IF NOT EXISTS  ocds.orchestrator_operation_step (
    process_id text,
    task_id text,
    operation_id text,
    step_date timestamp,
    request_data text,
    response_data text,
    json_params text,
    cp_id text,
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
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('PS', 'PS', 'MD', 'RT', 'AWARDPERIOD', 'awardPeriodEnd');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('PQ', 'PQ', 'MD', 'RT', 'AWARDPERIOD', 'awardPeriodEnd');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('EV', 'EV', 'MD', 'RT', 'AWARDPERIOD', 'awardPeriodEndEv');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('EV', 'EV', 'MD', 'OT', 'AWARDPERIOD', 'awardByBidEv');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('EV', 'EV', 'MD', 'OT', 'AWARDPERIOD', 'awardPeriodEndEv');

INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('PS', 'PS', 'TEST', 'TEST_RT', 'AWARDPERIOD', 'awardByBid');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('PQ', 'PQ', 'TEST', 'TEST_RT', 'AWARDPERIOD', 'awardByBid');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('EV', 'EV', 'TEST', 'TEST_RT', 'AWARDPERIOD', 'awardByBidEv');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('PQ', 'PS', 'TEST', 'TEST_RT', 'ENDSTAGE', 'startNewStage');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('EV', 'PQ', 'TEST', 'TEST_RT', 'ENDSTAGE', 'startNewStage');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('PIN', 'PN', 'TEST', 'TEST_RT', 'PLANNING', 'updatePIN');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('PS', 'PN', 'TEST', 'TEST_RT', 'PLANNING', 'updateCN');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('PS', 'PIN', 'TEST', 'TEST_RT', 'PLANNED', 'updateCN');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('PS', 'PS', 'TEST', 'TEST_RT', 'AWARDPERIOD', 'awardPeriodEnd');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('PQ', 'PQ', 'TEST', 'TEST_RT', 'AWARDPERIOD', 'awardPeriodEnd');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('EV', 'EV', 'TEST', 'TEST_RT', 'AWARDPERIOD', 'awardPeriodEndEv');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('EV', 'EV', 'TEST', 'TEST_OT', 'AWARDPERIOD', 'awardByBidEv');
INSERT INTO ocds.orchestrator_rules (new_stage, prev_stage, country, pmd, phase, operation_type)  VALUES ('EV', 'EV', 'TEST', 'TEST_OT', 'AWARDPERIOD', 'awardPeriodEndEv');

CREATE TABLE IF NOT EXISTS  ocds.orchestrator_stage_rules (
    country text,
    pmd text,
    operation_type text,
    stage text,
    primary key(country, pmd, operation_type));

INSERT INTO ocds.orchestrator_stage_rules (country, pmd, operation_type, stage)  VALUES ('MD', 'RT', 'createCN', 'PS');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, operation_type, stage)  VALUES ('MD', 'OT', 'createCN', 'EV');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, operation_type, stage)  VALUES ('TEST', 'TEST_RT', 'createCN', 'PS');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, operation_type, stage)  VALUES ('TEST', 'TEST_OT', 'createCN', 'EV');

INSERT INTO ocds.orchestrator_stage_rules (country, pmd, operation_type, stage)  VALUES ('MD', 'RT', 'createPIN', 'PIN');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, operation_type, stage)  VALUES ('MD', 'OT', 'createPIN', 'EV');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, operation_type, stage)  VALUES ('TEST', 'TEST_RT', 'createPIN', 'PIN');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, operation_type, stage)  VALUES ('TEST', 'TEST_OT', 'createPIN', 'EV');

INSERT INTO ocds.orchestrator_stage_rules (country, pmd, operation_type, stage)  VALUES ('MD', 'RT', 'createPN', 'PN');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, operation_type, stage)  VALUES ('MD', 'OT', 'createPN', 'EV');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, operation_type, stage)  VALUES ('TEST', 'TEST_RT', 'createPN', 'PN');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, operation_type, stage)  VALUES ('TEST', 'TEST_OT', 'createPN', 'EV');