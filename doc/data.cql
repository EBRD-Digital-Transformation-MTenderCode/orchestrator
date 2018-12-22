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

CREATE TABLE IF NOT EXISTS  ocds.orchestrator_check (
    id text,
    context text,
    primary key(id));

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

INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','createEI','ei');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','updateEI','ei');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','createFS','fs');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','updateFS','fs');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','createCN','createCN');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','updateCN','updateCN');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','createPN','createPN');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','updatePN','updatePN');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','createBid','submitTheBid');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','updateBid','submitTheBid');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','enquiry','enquiry');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','answer','answer');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','bidWithdrawn','bidWithdrawn');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','cnCancellation','tenderCancellation');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','pinCancellation','tenderCancellation');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','pnCancellation','tenderCancellation');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','awardByBid','awardByBid');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','createCan','createCan');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','updateCanDocs','updateCanDocs');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','cancelCan','cancelCan');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','createAC','createAC');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','updateAC','updateAC');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','updateBidDocs','updateBidDocs');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','issuingAC','issuingAC');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','finalUpdateAC','finalUpdateAC');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','buyerSigningAC','buyerSigningAC');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','supplierSigningAC','supplierSigningAC');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','OT','activationAC','activationAC');

INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','createEI','ei');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','updateEI','ei');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','createFS','fs');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','updateFS','fs');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','createCN','createCN');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','updateCN','updateCN');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','createPN','createPN');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','updatePN','updatePN');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','createBid','submitTheBid');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','updateBid','submitTheBid');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','enquiry','enquiry');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','answer','answer');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','bidWithdrawn','bidWithdrawn');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','cnCancellation','tenderCancellation');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','pinCancellation','tenderCancellation');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','pnCancellation','tenderCancellation');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','awardByBid','awardByBid');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','createCan','createCan');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','updateCanDocs','updateCanDocs');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','cancelCan','cancelCan');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','createAC','createAC');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','updateAC','updateAC');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','updateBidDocs','updateBidDocs');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','issuingAC','issuingAC');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','finalUpdateAC','finalUpdateAC');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','buyerSigningAC','buyerSigningAC');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','supplierSigningAC','supplierSigningAC');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_OT','activationAC','activationAC');

INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','createPN','PN','planning','planning','PN','createPN');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','updatePN','PN','planning','planning','PN','updatePN');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','submitTheBid','EV','tendering','tendering','EV','submitTheBid');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','enquiryPeriodEnd','EV','clarification','tendering','EV','enquiryPeriodEnd');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','tenderPeriodEnd','EV','tendering','awarding','EV','tenderPeriodEndEv');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','tenderPeriodEndAuction','EV','tendering','auction','EV','tenderPeriodEndAuction');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','auctionPeriodEnd','EV','auction','awarding','EV','auctionPeriodEnd');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','createCN','EV','clarification','clarification','EV','createCN');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','updateCN','EV','clarification','clarification','EV','updateCN');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','updateCN','PIN','planned','clarification','EV','createCNonPIN');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','updateCN','PN','planning','clarification','EV','createCNonPN');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','enquiry','EV','clarification','clarification','EV','createEnquiry');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','answer','EV','clarification','clarification','EV','addAnswer');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','answer','EV','suspended','clarification','EV','addAnswer');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','awardByBid','EV','awarding','awarding','EV','awardByBidEv');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','bidWithdrawn','EV','tendering','tendering','EV','bidWithdrawn');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','tenderCancellation','PN','planning','empty','PN','cancelPlan');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','tenderCancellation','EV','tendering','tendering','EV','cancellationStandstillPeriod');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','tenderCancellation','EV','awarding','awarding','EV','cancellationStandstillPeriod');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','tenderCancellation','EV','clarification','clarification','EV','cancellationStandstillPeriod');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','updateBidDocs','EV','awarding','awarding','EV','updateBidDocs');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','createCan','EV','awarding','awarding','EV','createCan');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','updateCanDocs','EV','awarding','awarding','EV','updateCanDocs');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','cancelCan','EV','awarding','awarding','EV','cancelCan');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','createAC','EV','awarding','contractProject','EV','createAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','updateAC','AC','contractProject','contractPreparation','AC','updateAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','updateAC','AC','contractPreparation','contractPreparation','AC','updateAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','issuingAC','AC','contractPreparation','contractIssuing','AC','issuingAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','finalUpdateAC','AC','contractIssuing','approvement','AC','finalUpdateAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','buyerSigningAC','AC','approvement','approvedContract','AC','buyerSigningAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','supplierSigningAC','AC','approvedContract','signedContract','AC','supplierSigningAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','verificationAC','AC','signedContract','verification','AC','verificationAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','treasuryApprovingAC','AC','verification','verified','AC','treasuryApprovingAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','activationAC','AC','verified','execution','AC','activationAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','OT','activationAC','AC','signedContract','execution','AC','activationAC');

INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','createPN','PN','planning','planning','PN','createPN');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','updatePN','PN','planning','planning','PN','updatePN');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','submitTheBid','EV','tendering','tendering','EV','submitTheBid');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','enquiryPeriodEnd','EV','clarification','tendering','EV','enquiryPeriodEnd');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','tenderPeriodEnd','EV','tendering','awarding','EV','tenderPeriodEndEv');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','tenderPeriodEndAuction','EV','tendering','auction','EV','tenderPeriodEndAuction');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','auctionPeriodEnd','EV','auction','awarding','EV','auctionPeriodEnd');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','createCN','EV','clarification','clarification','EV','createCN');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','updateCN','EV','clarification','clarification','EV','updateCN');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','updateCN','PIN','planned','clarification','EV','createCNonPIN');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','updateCN','PN','planning','clarification','EV','createCNonPN');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','enquiry','EV','clarification','clarification','EV','createEnquiry');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','answer','EV','clarification','clarification','EV','addAnswer');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','answer','EV','suspended','clarification','EV','addAnswer');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','awardByBid','EV','awarding','awarding','EV','awardByBidEv');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','bidWithdrawn','EV','tendering','tendering','EV','bidWithdrawn');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','tenderCancellation','PN','planning','empty','PN','cancelPlan');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','tenderCancellation','EV','tendering','tendering','EV','cancellationStandstillPeriod');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','tenderCancellation','EV','awarding','awarding','EV','cancellationStandstillPeriod');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','tenderCancellation','EV','clarification','clarification','EV','cancellationStandstillPeriod');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','updateBidDocs','EV','awarding','awarding','EV','updateBidDocs');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','createCan','EV','awarding','awarding','EV','createCan');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','updateCanDocs','EV','awarding','awarding','EV','updateCanDocs');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','cancelCan','EV','awarding','awarding','EV','cancelCan');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','createAC','EV','awarding','contractProject','EV','createAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','updateAC','AC','contractProject','contractPreparation','AC','updateAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','updateAC','AC','contractPreparation','contractPreparation','AC','updateAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','issuingAC','AC','contractPreparation','contractIssuing','AC','issuingAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','finalUpdateAC','AC','contractIssuing','approvement','AC','finalUpdateAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','buyerSigningAC','AC','approvement','approvedContract','AC','buyerSigningAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','supplierSigningAC','AC','approvedContract','signedContract','AC','supplierSigningAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','verificationAC','AC','signedContract','verification','AC','verificationAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','treasuryApprovingAC','AC','verification','verified','AC','treasuryApprovingAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','activationAC','AC','verified','execution','AC','activationAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_OT','activationAC','AC','signedContract','execution','AC','activationAC');

INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','createEI','ei');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','updateEI','ei');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','createFS','fs');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','updateFS','fs');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','createCN','createCN');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','updateCN','updateCN');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','createPN','createPN');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','updatePN','updatePN');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','createBid','submitTheBid');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','updateBid','submitTheBid');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','enquiry','enquiry');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','answer','answer');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','bidWithdrawn','bidWithdrawn');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','cnCancellation','tenderCancellation');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','pinCancellation','tenderCancellation');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','pnCancellation','tenderCancellation');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','awardByBid','awardByBid');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','createCan','createCan');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','updateCanDocs','updateCanDocs');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','cancelCan','cancelCan');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','createAC','createAC');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','updateAC','updateAC');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','updateBidDocs','updateBidDocs');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','issuingAC','issuingAC');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','finalUpdateAC','finalUpdateAC');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','buyerSigningAC','buyerSigningAC');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','supplierSigningAC','supplierSigningAC');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','SV','activationAC','activationAC');

INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','createEI','ei');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','updateEI','ei');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','createFS','fs');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','updateFS','fs');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','createCN','createCN');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','updateCN','updateCN');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','createPN','createPN');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','updatePN','updatePN');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','createBid','submitTheBid');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','updateBid','submitTheBid');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','enquiry','enquiry');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','answer','answer');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','bidWithdrawn','bidWithdrawn');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','cnCancellation','tenderCancellation');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','pinCancellation','tenderCancellation');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','pnCancellation','tenderCancellation');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','awardByBid','awardByBid');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','createCan','createCan');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','updateCanDocs','updateCanDocs');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','cancelCan','cancelCan');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','createAC','createAC');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','updateAC','updateAC');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','updateBidDocs','updateBidDocs');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','issuingAC','issuingAC');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','finalUpdateAC','finalUpdateAC');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','buyerSigningAC','buyerSigningAC');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','supplierSigningAC','supplierSigningAC');
INSERT INTO ocds.orchestrator_process_type (country, pmd, process, process_type) VALUES ('MD','TEST_SV','activationAC','activationAC');

INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','createPN','PN','planning','planning','PN','createPN');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','updatePN','PN','planning','planning','PN','updatePN');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','submitTheBid','EV','tendering','tendering','EV','submitTheBid');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','enquiryPeriodEnd','EV','clarification','tendering','EV','enquiryPeriodEnd');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','tenderPeriodEnd','EV','tendering','awarding','EV','tenderPeriodEndEv');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','tenderPeriodEndAuction','EV','tendering','auction','EV','tenderPeriodEndAuction');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','auctionPeriodEnd','EV','auction','awarding','EV','auctionPeriodEnd');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','createCN','EV','clarification','clarification','EV','createCN');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','updateCN','EV','clarification','clarification','EV','updateCN');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','updateCN','PIN','planned','clarification','EV','createCNonPIN');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','updateCN','PN','planning','clarification','EV','createCNonPN');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','enquiry','EV','clarification','clarification','EV','createEnquiry');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','answer','EV','clarification','clarification','EV','addAnswer');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','answer','EV','suspended','clarification','EV','addAnswer');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','awardByBid','EV','awarding','awarding','EV','awardByBidEv');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','bidWithdrawn','EV','tendering','tendering','EV','bidWithdrawn');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','tenderCancellation','PN','planning','empty','PN','cancelPlan');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','tenderCancellation','EV','tendering','tendering','EV','cancellationStandstillPeriod');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','tenderCancellation','EV','awarding','awarding','EV','cancellationStandstillPeriod');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','tenderCancellation','EV','clarification','clarification','EV','cancellationStandstillPeriod');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','updateBidDocs','EV','awarding','awarding','EV','updateBidDocs');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','createCan','EV','awarding','awarding','EV','createCan');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','updateCanDocs','EV','awarding','awarding','EV','updateCanDocs');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','cancelCan','EV','awarding','awarding','EV','cancelCan');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','createAC','EV','awarding','contractProject','EV','createAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','updateAC','AC','contractProject','contractPreparation','AC','updateAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','updateAC','AC','contractPreparation','contractPreparation','AC','updateAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','issuingAC','AC','contractPreparation','contractIssuing','AC','issuingAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','finalUpdateAC','AC','contractIssuing','approvement','AC','finalUpdateAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','buyerSigningAC','AC','approvement','approvedContract','AC','buyerSigningAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','supplierSigningAC','AC','approvedContract','signedContract','AC','supplierSigningAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','verificationAC','AC','signedContract','verification','AC','verificationAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','treasuryApprovingAC','AC','verification','verified','AC','treasuryApprovingAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','activationAC','AC','verified','execution','AC','activationAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','SV','activationAC','AC','signedContract','execution','AC','activationAC');

INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','createPN','PN','planning','planning','PN','createPN');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','updatePN','PN','planning','planning','PN','updatePN');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','submitTheBid','EV','tendering','tendering','EV','submitTheBid');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','enquiryPeriodEnd','EV','clarification','tendering','EV','enquiryPeriodEnd');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','tenderPeriodEnd','EV','tendering','awarding','EV','tenderPeriodEndEv');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','tenderPeriodEndAuction','EV','tendering','auction','EV','tenderPeriodEndAuction');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','auctionPeriodEnd','EV','auction','awarding','EV','auctionPeriodEnd');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','createCN','EV','clarification','clarification','EV','createCN');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','updateCN','EV','clarification','clarification','EV','updateCN');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','updateCN','PIN','planned','clarification','EV','createCNonPIN');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','updateCN','PN','planning','clarification','EV','createCNonPN');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','enquiry','EV','clarification','clarification','EV','createEnquiry');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','answer','EV','clarification','clarification','EV','addAnswer');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','answer','EV','suspended','clarification','EV','addAnswer');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','awardByBid','EV','awarding','awarding','EV','awardByBidEv');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','bidWithdrawn','EV','tendering','tendering','EV','bidWithdrawn');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','tenderCancellation','PN','planning','empty','PN','cancelPlan');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','tenderCancellation','EV','tendering','tendering','EV','cancellationStandstillPeriod');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','tenderCancellation','EV','awarding','awarding','EV','cancellationStandstillPeriod');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','tenderCancellation','EV','clarification','clarification','EV','cancellationStandstillPeriod');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','updateBidDocs','EV','awarding','awarding','EV','updateBidDocs');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','createCan','EV','awarding','awarding','EV','createCan');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','updateCanDocs','EV','awarding','awarding','EV','updateCanDocs');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','createAC','EV','awarding','contractProject','EV','createAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','cancelCan','EV','awarding','awarding','EV','cancelCan');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','updateAC','AC','contractProject','contractPreparation','AC','updateAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','updateAC','AC','contractPreparation','contractPreparation','AC','updateAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','issuingAC','AC','contractPreparation','contractIssuing','AC','issuingAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','finalUpdateAC','AC','contractIssuing','approvement','AC','finalUpdateAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','buyerSigningAC','AC','approvement','approvedContract','AC','buyerSigningAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','supplierSigningAC','AC','approvedContract','signedContract','AC','supplierSigningAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','verificationAC','AC','signedContract','verification','AC','verificationAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','treasuryApprovingAC','AC','verification','verified','AC','treasuryApprovingAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','activationAC','AC','verified','execution','AC','activationAC');
INSERT INTO ocds.orchestrator_rules (country,pmd,process_type,prev_stage,prev_phase,new_phase,new_stage,operation_type) VALUES ('MD','TEST_SV','activationAC','AC','signedContract','execution','AC','activationAC');