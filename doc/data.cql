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
 
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','awardByBid','PQ','AWARDING','AWARDING','PQ','awardByBid');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','awardByBid','PS','AWARDING','AWARDING','PS','awardByBid');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','awardByBid','PQ','AWARDING','AWARDING','PQ','awardByBid');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','awardByBid','PS','AWARDING','AWARDING','PS','awardByBid');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','awardByBid','EV','AWARDING','AWARDING','EV','awardByBidEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','awardByBid','EV','AWARDING','AWARDING','EV','awardByBidEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','awardByBid','EV','AWARDING','AWARDING','EV','awardByBidEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','awardByBid','EV','AWARDING','AWARDING','EV','awardByBidEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','awardPeriodEnd','PQ','AWARDING','TRANSITION','PQ','awardPeriodEnd');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','awardPeriodEnd','PS','AWARDING','TRANSITION','PS','awardPeriodEnd');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','awardPeriodEnd','PQ','AWARDING','TRANSITION','PQ','awardPeriodEnd');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','awardPeriodEnd','PS','AWARDING','TRANSITION','PS','awardPeriodEnd');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','awardPeriodEnd','EV','AWARDING','CONTRACTING','EV','awardPeriodEndEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','awardPeriodEnd','EV','AWARDING','CONTRACTING','EV','awardPeriodEndEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','awardPeriodEnd','EV','AWARDING','CONTRACTING','EV','awardPeriodEndEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','awardPeriodEnd','EV','AWARDING','CONTRACTING','EV','awardPeriodEndEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','bidWithdrawn','EV','TENDERING','TENDERING','EV','bidWithdrawn');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','bidWithdrawn','EV','TENDERING','TENDERING','EV','bidWithdrawn');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','bidWithdrawn','EV','TENDERING','TENDERING','EV','bidWithdrawn');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','bidWithdrawn','PQ','TENDERING','TENDERING','PQ','bidWithdrawn');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','bidWithdrawn','PS','TENDERING','TENDERING','PS','bidWithdrawn');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','bidWithdrawn','EV','TENDERING','TENDERING','EV','bidWithdrawn');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','bidWithdrawn','PQ','TENDERING','TENDERING','PQ','bidWithdrawn');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','bidWithdrawn','PS','TENDERING','TENDERING','PS','bidWithdrawn');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','createCN','EV','TENDERING','TENDERING','EV','createCN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','createCN','EV','TENDERING','TENDERING','EV','createCN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','createCN','PS','TENDERING','TENDERING','PS','createCN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','createCN','PS','TENDERING','TENDERING','PS','createCN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','updateCN','PIN','PLANNED','TENDERING','EV','createCNonPIN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','updateCN','PIN','PLANNED','TENDERING','EV','createCNonPIN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','updateCN','PIN','PLANNED','TENDERING','PS','createCNonPIN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','updateCN','PIN','PLANNED','TENDERING','PS','createCNonPIN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','updateCN','PN','PLANNING','TENDERING','EV','createCNonPN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','updateCN','PN','PLANNING','TENDERING','EV','createCNonPN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','updateCN','PN','PLANNING','TENDERING','PS','createCNonPN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','updateCN','PN','PLANNING','TENDERING','PS','createCNonPN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','enquiry','EV','TENDERING','TENDERING','EV','createEnquiry');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','enquiry','PQ','TENDERING','TENDERING','PQ','createEnquiry');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','enquiry','PS','TENDERING','TENDERING','PS','createEnquiry');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','enquiry','EV','TENDERING','TENDERING','EV','createEnquiry');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','enquiry','EV','TENDERING','TENDERING','EV','createEnquiry');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','enquiry','PQ','TENDERING','TENDERING','PQ','createEnquiry');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','enquiry','PS','TENDERING','TENDERING','PS','createEnquiry');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','enquiry','EV','TENDERING','TENDERING','EV','createEnquiry');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','createPIN','PIN','PLANNED','PLANNED','PIN','createPIN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','createPIN','PIN','PLANNED','PLANNED','PIN','createPIN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','createPIN','PIN','PLANNED','PLANNED','PIN','createPIN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','createPIN','PIN','PLANNED','PLANNED','PIN','createPIN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','createPN','PN','PLANNING','PLANNING','PN','createPN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','createPN','PN','PLANNING','PLANNING','PN','createPN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','createPN','PN','PLANNING','PLANNING','PN','createPN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','createPN','PN','PLANNING','PLANNING','PN','createPN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','updatePN','PN','PLANNING','PLANNING','PN','updatePN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','updatePN','PN','PLANNING','PLANNING','PN','updatePN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','updatePN','PN','PLANNING','PLANNING','PN','updatePN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','updatePN','PN','PLANNING','PLANNING','PN','updatePN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','startNewStage','PQ','TRANSITION','TENDERING','EV','startNewStage');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','startNewStage','PS','TRANSITION','TENDERING','PQ','startNewStage');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','startNewStage','PQ','TRANSITION','TENDERING','EV','startNewStage');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','startNewStage','PS','TRANSITION','TENDERING','PQ','startNewStage');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','submitTheBid','EV','TENDERING','TENDERING','EV','submitTheBid');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','submitTheBid','EV','TENDERING','TENDERING','EV','submitTheBid');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','submitTheBid','PQ','TENDERING','TENDERING','PQ','submitTheBid');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','submitTheBid','PS','TENDERING','TENDERING','PS','submitTheBid');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','submitTheBid','EV','TENDERING','TENDERING','EV','submitTheBid');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','submitTheBid','EV','TENDERING','TENDERING','EV','submitTheBid');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','submitTheBid','PQ','TENDERING','TENDERING','PQ','submitTheBid');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','submitTheBid','PS','TENDERING','TENDERING','PS','submitTheBid');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','tenderPeriodEnd','PQ','TENDERING','AWARDING','PQ','tenderPeriodEnd');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','tenderPeriodEnd','PS','TENDERING','AWARDING','PS','tenderPeriodEnd');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','tenderPeriodEnd','PQ','TENDERING','AWARDING','PQ','tenderPeriodEnd');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','tenderPeriodEnd','PS','TENDERING','AWARDING','PS','tenderPeriodEnd');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','tenderPeriodEnd','EV','TENDERING','AWARDING','EV','tenderPeriodEndEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','tenderPeriodEnd','EV','TENDERING','AWARDING','EV','tenderPeriodEndEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','tenderPeriodEnd','EV','TENDERING','AWARDING','EV','tenderPeriodEndEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','tenderPeriodEnd','EV','TENDERING','AWARDING','EV','tenderPeriodEndEv');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','updateCN','EV','TENDERING','TENDERING','EV','updateCN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','updateCN','EV','TENDERING','TENDERING','EV','updateCN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','updateCN','PS','TENDERING','TENDERING','PS','updateCN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','updateCN','PS','TENDERING','TENDERING','PS','updateCN');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','updateCN','EV','TENDERING','TENDERING','EV','updateTenderPeriod');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','RT','updateCN','PQ','TENDERING','TENDERING','PQ','updateTenderPeriod');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','updateCN','EV','TENDERING','TENDERING','EV','updateTenderPeriod');
INSERT INTO orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_RT','updateCN','PQ','TENDERING','TENDERING','PQ','updateTenderPeriod');
