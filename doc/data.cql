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

CREATE TABLE IF NOT EXISTS  ocds.orchestrator_rules (
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
 
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','createPIN','PIN','PLANNED','PLANNED','PIN','createPIN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','createPIN','PIN','PLANNED','PLANNED','PIN','createPIN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','createPN','PN','PLANNING','PLANNING','PN','createPN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','createPN','PN','PLANNING','PLANNING','PN','createPN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','updatePN','PN','PLANNING','PLANNING','PN','updatePN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','updatePN','PN','PLANNING','PLANNING','PN','updatePN');

INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','submitTheBid','EV','TENDERING','TENDERING','EV','submitTheBid');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','submitTheBid','EV','TENDERING','TENDERING','EV','submitTheBid');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','tenderPeriodEnd','EV','TENDERING','AWARDING','EV','tenderPeriodEndEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','tenderPeriodEnd','EV','TENDERING','AWARDING','EV','tenderPeriodEndEv');

INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','createCN','EV','ENQUIRING','ENQUIRING','EV','createCN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','createCN','EV','ENQUIRING','ENQUIRING','EV','createCN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','updateCN','EV','ENQUIRING','ENQUIRING','EV','updateCN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','updateCN','EV','ENQUIRING','ENQUIRING','EV','updateCN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','updateCN','PIN','PLANNED','ENQUIRING','EV','createCNonPIN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','updateCN','PIN','PLANNED','ENQUIRING','EV','createCNonPIN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','updateCN','PN','PLANNING','ENQUIRING','EV','createCNonPN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','updateCN','PN','PLANNING','ENQUIRING','EV','createCNonPN');

INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','enquiry','EV','ENQUIRING','ENQUIRING','EV','createEnquiry');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','enquiry','EV','ENQUIRING','ENQUIRING','EV','createEnquiry');

INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','answer','EV','ENQUIRING','ENQUIRING','EV','addAnswer');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','answer','EV','ENQUIRING','ENQUIRING','EV','addAnswer');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','answer','EV','SUSPENDED','ENQUIRING','EV','addAnswer');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','answer','EV','SUSPENDED','ENQUIRING','EV','addAnswer');

INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','awardByBid','EV','AWARDING','AWARDING','EV','awardByBidEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','awardByBid','EV','AWARDING','AWARDING','EV','awardByBidEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','awardPeriodEnd','EV','AWARDING','CONTRACTING','EV','awardPeriodEndEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','awardPeriodEnd','EV','AWARDING','CONTRACTING','EV','awardPeriodEndEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','bidWithdrawn','EV','TENDERING','TENDERING','EV','bidWithdrawn');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','bidWithdrawn','EV','TENDERING','TENDERING','EV','bidWithdrawn');

INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','tenderCancellation','PN','PLANNING','PLANNING','PN','cancelPlan');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','tenderCancellation','PIN','PLANNED','PLANNED','PIN','cancelPlan');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','tenderCancellation','PN','PLANNING','PLANNING','PN','cancelPlan');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','tenderCancellation','PIN','PLANNED','PLANNED','PIN','cancelPlan');

INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','tenderCancellation','EV','TENDERING','TENDERING','EV','cancelTenderEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','tenderCancellation','EV','AWARDING','AWARDING','EV','cancelTenderEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','tenderCancellation','EV','TENDERING','TENDERING','EV','cancelTenderEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','tenderCancellation','EV','AWARDING','AWARDING','EV','cancelTenderEv');

