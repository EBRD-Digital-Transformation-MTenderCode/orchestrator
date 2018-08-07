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

CREATE TABLE IF NOT EXISTS  ocds.orchestrator_stage_rules (
    country text,
    pmd text,
    process_type text,
    prev_stage text,
    new_stage text,
    prev_phase text,
    new_phase text,
    operation_type text,
    primary key((country, pmd, process_type), prev_stage, prev_phase)
 );
 
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'createPIN', 'PIN', 'PIN', 'PLANNED', 'PLANNED', 'createPIN');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'OT', 'createPIN', 'PIN', 'PIN', 'PLANNED', 'PLANNED', 'createPIN'); 
 
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'createPN', 'PN', 'PN', 'PLANNING', 'PLANNING', 'createPN');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'OT', 'createPN', 'PN', 'PN', 'PLANNING', 'PLANNING', 'createPN');
    
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'createCN', 'PS', 'PS', 'TENDERING', 'TENDERING', 'createCN');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'OT', 'createCN', 'EV', 'EV', 'TENDERING', 'TENDERING', 'createCN');
    
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'OT', 'updateCN', 'PN', 'EV', 'PLANNING', 'TENDERING', 'createCNonPN');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'OT', 'updateCN', 'PIN', 'EV', 'PLANNED', 'TENDERING', 'createCNonPIN');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'updateCN', 'PN', 'PS', 'PLANNING', 'TENDERING', 'createCNonPN');    
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'updateCN', 'PIN', 'PS', 'PLANNED', 'TENDERING', 'createCNonPIN');

INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'OT', 'updateCN', 'EV', 'EV', 'TENDERING', 'TENDERING', 'updateCN');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'updateCN', 'PS', 'PS', 'TENDERING', 'TENDERING', 'updateCN');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'updateCN', 'PQ', 'PQ', 'TENDERING', 'TENDERING', 'updateCN');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'updateCN', 'EV', 'EV', 'TENDERING', 'TENDERING', 'updateCN');

INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'submitTheBid', 'PS', 'PS', 'TENDERING', 'TENDERING', 'submitTheBid');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'submitTheBid', 'PQ', 'PQ', 'TENDERING', 'TENDERING', 'submitTheBid');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'submitTheBid', 'EV', 'EV', 'TENDERING', 'TENDERING', 'submitTheBid');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'OT', 'submitTheBid', 'EV', 'EV', 'TENDERING', 'TENDERING', 'submitTheBid');

INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'bidWithdrown', 'PS', 'PS', 'TENDERING', 'TENDERING', 'bidWithdrown');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'bidWithdrown', 'PQ', 'PQ', 'TENDERING', 'TENDERING', 'bidWithdrown');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'bidWithdrown', 'EV', 'EV', 'TENDERING', 'TENDERING', 'bidWithdrown');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'OT', 'bidWithdrown', 'EV', 'EV', 'TENDERING', 'TENDERING', 'bidWithdrown');

INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'enquiry', 'PS', 'PS', 'TENDERING', 'TENDERING', 'createEnquiry');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'enquiry', 'PQ', 'PQ', 'TENDERING', 'TENDERING', 'createEnquiry');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'enquiry', 'EV', 'EV', 'TENDERING', 'TENDERING', 'createEnquiry');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'OT', 'enquiry', 'EV', 'EV', 'TENDERING', 'TENDERING', 'createEnquiry');

INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'tenderPeriodEnd', 'PS', 'PS', 'TENDERING', 'AWARDING', 'tenderPeriodEnd');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'tenderPeriodEnd', 'PQ', 'PQ', 'TENDERING', 'AWARDING', 'tenderPeriodEnd');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'tenderPeriodEnd', 'EV', 'EV', 'TENDERING', 'AWARDING', 'tenderPeriodEndEv');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'OT', 'tenderPeriodEnd', 'EV', 'EV', 'TENDERING', 'AWARDING', 'tenderPeriodEndEv');

INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'awardByBid', 'PS', 'PS', 'AWARDING', 'AWARDING', 'awardByBid');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'awardByBid', 'PQ', 'PQ', 'AWARDING', 'AWARDING', 'awardByBid');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'awardByBid', 'EV', 'EV', 'AWARDING', 'AWARDING', 'awardByBidEv');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'OT', 'awardByBid', 'EV', 'EV', 'AWARDING', 'AWARDING', 'awardByBidEv');

INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'awardPeriodEnd', 'PS', 'PS', 'AWARDING', 'TRANSITION', 'awardPeriodEnd');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'awardPeriodEnd', 'PQ', 'PQ', 'AWARDING', 'TRANSITION', 'awardPeriodEnd');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'awardPeriodEnd', 'EV', 'EV', 'AWARDING', 'CONTRACTING', 'awardPeriodEndEv');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'OT', 'awardPeriodEnd', 'EV', 'EV', 'AWARDING', 'CONTRACTING', 'awardPeriodEndEv');

INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'startNewStage', 'PS', 'PS', 'TRANSITION', 'TENDERING', 'startNewStage');
INSERT INTO ocds.orchestrator_stage_rules (country, pmd, process_type, prev_stage, new_stage, prev_phase, new_phase, operation_type) VALUES ('MD', 'RT', 'startNewStage', 'PQ', 'PQ', 'TRANSITION', 'TENDERING', 'startNewStage');


SELECT * FROM ocds.orchestrator_stage_rules WHERE country='MD' AND pmd='OT' AND process_type='createPIN';