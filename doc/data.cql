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

    planning("planning"),
    planned("planned"),
    clarification("clarification"),
    tendering("tendering"),
    CANCELLATION("cancellation"),
    suspended("suspended"),
    awarding("awarding"),
    AWARDED_STANDSTILL("awardedStandStill"),
    AWARDED_suspended("awardedsuspended"),
    AWARDED_CONTRACT_PREPARATION("awardedContractPreparation"),
    COMPLETE("complete"),
    EMPTY("empty");

INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','createPIN','PIN','planned','planned','PIN','createPIN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','createPN','PN','planning','planning','PN','createPN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','updatePN','PN','planning','planning','PN','updatePN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','submitTheBid','EV','tendering','tendering','EV','submitTheBid');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','tenderPeriodEnd','EV','tendering','awarding','EV','tenderPeriodEndEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','createCN','EV','clarification','clarification','EV','createCN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','updateCN','EV','clarification','clarification','EV','updateCN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','updateCN','PIN','planned','clarification','EV','createCNonPIN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','updateCN','PN','planning','clarification','EV','createCNonPN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','enquiry','EV','clarification','clarification','EV','createEnquiry');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','answer','EV','clarification','clarification','EV','addAnswer');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','answer','EV','suspended','clarification','EV','addAnswer');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','answer','EV','suspended','tendering','EV','addAnswer');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','awardByBid','EV','awarding','awarding','EV','awardByBidEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','awardPeriodEnd','EV','awarding','awardedContractPreparation','EV','awardPeriodEndEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','bidWithdrawn','EV','tendering','tendering','EV','bidWithdrawn');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','tenderCancellation','PN','planning','planning','PN','cancelPlan');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','tenderCancellation','PIN','planned','planned','PIN','cancelPlan');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','tenderCancellation','EV','tendering','tendering','EV','cancelTenderEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','tenderCancellation','EV','awarding','awarding','EV','cancelTenderEv');

INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','createPIN','PIN','planned','planned','PIN','createPIN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','createPN','PN','planning','planning','PN','createPN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','updatePN','PN','planning','planning','PN','updatePN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','submitTheBid','EV','tendering','tendering','EV','submitTheBid');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','tenderPeriodEnd','EV','tendering','awarding','EV','tenderPeriodEndEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','createCN','EV','clarification','clarification','EV','createCN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','updateCN','EV','clarification','clarification','EV','updateCN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','updateCN','PIN','planned','clarification','EV','createCNonPIN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','updateCN','PN','planning','clarification','EV','createCNonPN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','enquiry','EV','clarification','clarification','EV','createEnquiry');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','answer','EV','clarification','clarification','EV','addAnswer');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','answer','EV','suspended','clarification','EV','addAnswer');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','answer','EV','suspended','tendering','EV','addAnswer');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','awardByBid','EV','awarding','awarding','EV','awardByBidEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','awardPeriodEnd','EV','awarding','awardedContractPreparation','EV','awardPeriodEndEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','bidWithdrawn','EV','tendering','tendering','EV','bidWithdrawn');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','tenderCancellation','PN','planning','planning','PN','cancelPlan');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','tenderCancellation','PIN','planned','planned','PIN','cancelPlan');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','tenderCancellation','EV','tendering','tendering','EV','cancelTenderEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','tenderCancellation','EV','awarding','awarding','EV','cancelTenderEv');
