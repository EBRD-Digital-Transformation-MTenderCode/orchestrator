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

 CREATE TABLE IF NOT EXISTS  ocds.orchestrator_process_type (
     country text,
     pmd text,
     process text,
     process_type text,
     primary key((country, pmd, process))
  );

INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','createEI','ei');
INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','createEI','ei');
INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','updateEI','ei');
INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','updateEI','ei');

INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','createFS','fs');
INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','createFS','fs');
INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','updateFS','fs');
INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','updateFS','fs');

INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','createCN','createCN');
INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','createCN','createCN');
INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','updateCN','updateCN');
INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','updateCN','updateCN');

INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','createPN','createPN');
INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','createPN','createPN');
INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','updatePN','updatePN');
INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','updatePN','updatePN');

INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','createBid','submitTheBid');
INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','createBid','submitTheBid');
INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','updateBid','submitTheBid');
INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','updateBid','submitTheBid');

INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','createEnquiry','enquiry');
INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','createEnquiry','enquiry');
INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','addAnswer','answer');
INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','addAnswer','answer');

INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','endAwardPeriod','awardPeriodEnd');
INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','endAwardPeriod','awardPeriodEnd');

INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','bidWithdrawn','bidWithdrawn');
INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','bidWithdrawn','bidWithdrawn');
INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','cnCancellation','tenderCancellation');
INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','cnCancellation','tenderCancellation');
INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','pinCancellation','tenderCancellation');
INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','pinCancellation','tenderCancellation');
INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','pnCancellation','tenderCancellation');
INSERT INTO orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','pnCancellation','tenderCancellation');


INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','createPIN','PIN','PLANNED','PLANNED','PIN','createPIN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','createPN','PN','PLANNING','PLANNING','PN','createPN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','updatePN','PN','PLANNING','PLANNING','PN','updatePN');

INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','submitTheBid','EV','TENDERING','TENDERING','EV','submitTheBid');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','tenderPeriodEnd','EV','TENDERING','AWARDING','EV','tenderPeriodEndEv');

INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','createCN','EV','CLARIFICATION','CLARIFICATION','EV','createCN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','updateCN','EV','CLARIFICATION','CLARIFICATION','EV','updateCN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','updateCN','PIN','PLANNED','CLARIFICATION','EV','createCNonPIN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','updateCN','PN','PLANNING','CLARIFICATION','EV','createCNonPN');

INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','enquiry','EV','CLARIFICATION','CLARIFICATION','EV','createEnquiry');

INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','answer','EV','CLARIFICATION','CLARIFICATION','EV','addAnswer');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','answer','EV','SUSPENDED','CLARIFICATION','EV','addAnswer');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','answer','EV','SUSPENDED','TENDERING','EV','addAnswer');


INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','awardByBid','EV','AWARDING','AWARDING','EV','awardByBidEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','awardPeriodEnd','EV','AWARDING','CONTRACTING','EV','awardPeriodEndEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','bidWithdrawn','EV','TENDERING','TENDERING','EV','bidWithdrawn');

INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','tenderCancellation','PN','PLANNING','PLANNING','PN','cancelPlan');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','tenderCancellation','PIN','PLANNED','PLANNED','PIN','cancelPlan');

INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','tenderCancellation','EV','TENDERING','TENDERING','EV','cancelTenderEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','tenderCancellation','EV','AWARDING','AWARDING','EV','cancelTenderEv');

