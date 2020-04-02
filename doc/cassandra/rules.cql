INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'NP', 'createProtocol', 'NP', 'NP', 'awarding', 'awarding', 'createProtocol');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'createCN', 'EV', 'EV', 'clarification', 'clarification', 'createCN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'updatePN', 'PN', 'PN', 'planning', 'planning', 'updatePN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'submitTheBid', 'EV', 'EV', 'tendering', 'tendering', 'submitTheBid');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'issuingAC', 'AC', 'AC', 'contractPreparation', 'issued', 'issuingAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'supplierSigningAC', 'AC', 'AC', 'approved', 'signed', 'supplierSigningAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'treasuryApprovingAC', 'AC', 'AC', 'verification', 'verified', 'treasuryApprovingAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'updateCN', 'EV', 'EV', 'clarification', 'clarification', 'updateCN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'updateCN', 'PIN', 'EV', 'planned', 'clarification', 'createCNonPIN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'updateCN', 'PN', 'EV', 'planning', 'clarification', 'createCNonPN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OP', 'updateCnOp', 'PN', 'NP', 'planning', 'negotiation', 'createNegotiationCnOnPn');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'auctionPeriodEnd', 'EV', 'EV', 'auction', 'awarding', 'auctionPeriodEnd');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'DA', 'updateCnOp', 'PN', 'NP', 'planning', 'negotiation', 'createNegotiationCnOnPn');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'finalUpdateAC', 'AC', 'AC', 'issued', 'approvement', 'finalUpdateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OP', 'evaluateAward', 'NP', 'NP', 'awarding', 'awarding', 'evaluateAward');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'evaluateAward', 'EV', 'EV', 'awarding', 'awarding', 'evaluateAward');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'NP', 'createAC', 'NP', 'NP', 'awarding', 'contractProject', 'createAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'tenderPeriodEndAuction', 'EV', 'EV', 'tendering', 'auction', 'tenderPeriodEndAuction');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'DA', 'finalUpdateAC', 'AC', 'AC', 'issued', 'approvement', 'finalUpdateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'cancelCan', 'EV', 'EV', 'awarding', 'awarding', 'cancelCan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'updateCN', 'EV', 'EV', 'clarification', 'clarification', 'updateCN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'updateCN', 'PIN', 'EV', 'planned', 'clarification', 'createCNonPIN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'updateCN', 'PN', 'EV', 'planning', 'clarification', 'createCNonPN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'cancelCan', 'EV', 'EV', 'awarding', 'awarding', 'cancelCan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'enquiryPeriodEnd', 'EV', 'EV', 'clarification', 'tendering', 'enquiryPeriodEnd');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'treasuryApprovingAC', 'AC', 'AC', 'verification', 'verified', 'treasuryApprovingAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'createProtocol', 'EV', 'EV', 'awarding', 'awarding', 'createProtocol');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'createProtocol', 'NP', 'NP', 'awarding', 'awarding', 'createProtocol');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'DA', 'createProtocol', 'NP', 'NP', 'awarding', 'awarding', 'createProtocol');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_NP', 'cancelProtocol', 'NP', 'NP', 'awarding', 'awarding', 'cancelProtocol');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'NP', 'activationAC', 'AC', 'AC', 'signedContract', 'execution', 'activationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'NP', 'activationAC', 'AC', 'AC', 'verified', 'execution', 'activationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'updatePN', 'PN', 'PN', 'planning', 'planning', 'updatePN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'verificationAC', 'AC', 'AC', 'signed', 'verification', 'verificationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'createAC', 'EV', 'EV', 'awarding', 'contractProject', 'createAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'verificationAC', 'AC', 'AC', 'signed', 'verification', 'verificationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'createAC', 'EV', 'EV', 'awarding', 'contractProject', 'createAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'submitTheBid', 'EV', 'EV', 'tendering', 'tendering', 'submitTheBid');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'updateCN', 'EV', 'EV', 'clarification', 'clarification', 'updateCN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'updateCN', 'PIN', 'EV', 'planned', 'clarification', 'createCNonPIN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'updateCN', 'PN', 'EV', 'planning', 'clarification', 'createCNonPN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_DA', 'evaluateAward', 'NP', 'NP', 'awarding', 'awarding', 'evaluateAward');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_NP', 'acClarification', 'AC', 'AC', 'verification', '', 'processAcClarification');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_DA', 'createAC', 'NP', 'NP', 'awarding', 'contractProject', 'createAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OP', 'createProtocol', 'NP', 'NP', 'awarding', 'awarding', 'createProtocol');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'createCan', 'EV', 'EV', 'awarding', 'awarding', 'createCan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'updateCanDocs', 'EV', 'EV', 'awarding', 'awarding', 'updateCanDocs');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'updateBidDocs', 'EV', 'EV', 'awarding', 'awarding', 'updateBidDocs');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'cancelCan', 'EV', 'EV', 'awarding', 'awarding', 'cancelCan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'NP', 'finalUpdateAC', 'AC', 'AC', 'issued', 'approvement', 'finalUpdateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'finalUpdateAC', 'AC', 'AC', 'issued', 'approvement', 'finalUpdateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'cancelCan', 'EV', 'EV', 'awarding', 'awarding', 'cancelCan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'supplierSigningAC', 'AC', 'AC', 'approved', 'signed', 'supplierSigningAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OP', 'verificationAC', 'AC', 'AC', 'signed', 'verification', 'verificationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'answer', 'EV', 'EV', 'clarification', 'clarification', 'addAnswer');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'answer', 'EV', 'EV', 'suspended', 'clarification', 'addAnswer');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'DA', 'evaluateAward', 'NP', 'NP', 'awarding', 'awarding', 'evaluateAward');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'DA', 'verificationAC', 'AC', 'AC', 'signed', 'verification', 'verificationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'enquiryPeriodEnd', 'EV', 'EV', 'clarification', 'tendering', 'enquiryPeriodEnd');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'tenderPeriodEndAuction', 'EV', 'EV', 'tendering', 'auction', 'tenderPeriodEndAuction');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'confirmCan', 'EV', 'EV', 'awarding', 'awarding', 'confirmCan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'issuingAC', 'AC', 'AC', 'contractPreparation', 'issued', 'issuingAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'startConsiderByAward', 'EV', 'EV', 'awarding', 'awarding', 'doAwardConsideration');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'tenderPeriodEndAuction', 'EV', 'EV', 'tendering', 'auction', 'tenderPeriodEndAuction');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'startConsiderByAward', 'EV', 'EV', 'awarding', 'awarding', 'doAwardConsideration');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'confirmCan', 'EV', 'EV', 'awarding', 'awarding', 'confirmCan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'enquiryPeriodEnd', 'EV', 'EV', 'clarification', 'tendering', 'enquiryPeriodEnd');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'updateAC', 'AC', 'AC', 'contractPreparation', 'contractPreparation', 'updateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'updateAC', 'AC', 'AC', 'contractProject', 'contractPreparation', 'updateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'DA', 'buyerSigningAC', 'AC', 'AC', 'approvement', 'approved', 'buyerSigningAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_DA', 'updatePN', 'PN', 'PN', 'planning', 'planning', 'updatePN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_NP', 'createAC', 'NP', 'NP', 'awarding', 'contractProject', 'createAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'awardByBid', 'EV', 'EV', 'awarding', 'awarding', 'awardByBidEv');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_DA', 'createProtocol', 'NP', 'NP', 'awarding', 'awarding', 'createProtocol');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OP', 'createAwardOp', 'NP', 'NP', 'awarding', 'awarding', 'createAward');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OP', 'createAwardOp', 'NP', 'NP', 'negotiation', 'awarding', 'startAwardPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'updatePN', 'PN', 'PN', 'planning', 'planning', 'updatePN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_DA', 'createPN', '', 'PN', '', 'planning', 'createPN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'createProtocol', 'EV', 'EV', 'awarding', 'awarding', 'createProtocol');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'acClarification', 'AC', 'AC', 'verification', '', 'processAcClarification');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'confirmCan', 'EV', 'EV', 'awarding', 'awarding', 'confirmCan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'enquiry', 'EV', 'EV', 'clarification', 'clarification', 'createEnquiry');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'createCN', 'EV', 'EV', 'clarification', 'clarification', 'createCN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_DA', 'buyerSigningAC', 'AC', 'AC', 'approvement', 'approved', 'buyerSigningAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'updatePN', 'PN', 'PN', 'planning', 'planning', 'updatePN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'submitTheBid', 'EV', 'EV', 'tendering', 'tendering', 'submitTheBid');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OP', 'updatePN', 'PN', 'PN', 'planning', 'planning', 'updatePN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'tenderPeriodEnd', 'EV', 'EV', 'tendering', 'awarding', 'tenderPeriodEndEv');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'auctionPeriodEnd', 'EV', 'EV', 'auction', 'awarding', 'auctionPeriodEnd');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'NP', 'createPN', '', 'PN', '', 'planning', 'createPN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'createCan', 'EV', 'EV', 'awarding', 'awarding', 'createCan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'auctionPeriodEnd', 'EV', 'EV', 'auction', 'awarding', 'auctionPeriodEnd');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'tenderPeriodEnd', 'EV', 'EV', 'tendering', 'awarding', 'tenderPeriodEndEv');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'NP', 'buyerSigningAC', 'AC', 'AC', 'approvement', 'approved', 'buyerSigningAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OP', 'issuingAC', 'AC', 'AC', 'contractPreparation', 'issued', 'issuingAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'enquiry', 'EV', 'EV', 'clarification', 'clarification', 'createEnquiry');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'tenderPeriodEnd', 'EV', 'EV', 'tendering', 'awarding', 'tenderPeriodEndEv');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_NP', 'buyerSigningAC', 'AC', 'AC', 'approvement', 'approved', 'buyerSigningAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_NP', 'tenderCancellation', 'NP', 'NP', 'awarding', 'awarding', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_NP', 'tenderCancellation', 'NP', 'NP', 'negotiation', 'negotiation', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_NP', 'tenderCancellation', 'PN', 'PN', 'planning', 'empty', 'cancelPlan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'tenderCancellation', 'EV', 'EV', 'awarding', 'awarding', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'tenderCancellation', 'EV', 'EV', 'clarification', 'clarification', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'tenderCancellation', 'EV', 'EV', 'tendering', 'tendering', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'tenderCancellation', 'PN', 'PN', 'planning', 'empty', 'cancelPlan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'DA', 'updatePN', 'PN', 'PN', 'planning', 'planning', 'updatePN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'answer', 'EV', 'EV', 'clarification', 'clarification', 'addAnswer');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'answer', 'EV', 'EV', 'suspended', 'clarification', 'addAnswer');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'submitTheBid', 'EV', 'EV', 'tendering', 'tendering', 'submitTheBid');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'NP', 'updateCnOp', 'PN', 'NP', 'planning', 'negotiation', 'createNegotiationCnOnPn');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_DA', 'acRejection', 'AC', 'AC', 'verification', '', 'processAcRejection');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'updateCN', 'EV', 'EV', 'clarification', 'clarification', 'updateCN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'updateCN', 'PIN', 'EV', 'planned', 'clarification', 'createCNonPIN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'updateCN', 'PN', 'EV', 'planning', 'clarification', 'createCNonPN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'tenderPeriodEndAuction', 'EV', 'EV', 'tendering', 'auction', 'tenderPeriodEndAuction');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'cancelCan', 'EV', 'EV', 'awarding', 'awarding', 'cancelCan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'tenderCancellation', 'EV', 'EV', 'awarding', 'awarding', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'tenderCancellation', 'EV', 'EV', 'clarification', 'clarification', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'tenderCancellation', 'EV', 'EV', 'tendering', 'tendering', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'tenderCancellation', 'PN', 'PN', 'planning', 'empty', 'cancelPlan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'updateCanDocs', 'EV', 'EV', 'awarding', 'awarding', 'updateCanDocs');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_NP', 'createProtocol', 'NP', 'NP', 'awarding', 'awarding', 'createProtocol');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'NP', 'supplierSigningAC', 'AC', 'AC', 'approved', 'signed', 'supplierSigningAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_NP', 'createAwardOp', 'NP', 'NP', 'awarding', 'awarding', 'createAward');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_NP', 'createAwardOp', 'NP', 'NP', 'negotiation', 'awarding', 'startAwardPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'DA', 'tenderCancellation', 'NP', 'NP', 'awarding', 'awarding', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'DA', 'tenderCancellation', 'NP', 'NP', 'clarification', 'clarification', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'DA', 'tenderCancellation', 'NP', 'NP', 'negotiation', 'negotiation', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'DA', 'tenderCancellation', 'NP', 'NP', 'tendering', 'tendering', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'DA', 'tenderCancellation', 'PN', 'PN', 'planning', 'empty', 'cancelPlan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'acClarification', 'AC', 'AC', 'verification', '', 'processAcClarification');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'acRejection', 'AC', 'AC', 'verification', '', 'processAcRejection');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'updateCanDocs', 'EV', 'EV', 'awarding', 'awarding', 'updateCanDocs');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'DA', 'confirmCan', 'NP', 'NP', 'awarding', 'awarding', 'confirmCan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'issuingAC', 'AC', 'AC', 'contractPreparation', 'issued', 'issuingAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'verificationAC', 'AC', 'AC', 'signed', 'verification', 'verificationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'enquiryPeriodEnd', 'EV', 'EV', 'clarification', 'tendering', 'enquiryPeriodEnd');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OP', 'tenderCancellation', 'NP', 'NP', 'awarding', 'awarding', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OP', 'tenderCancellation', 'NP', 'NP', 'negotiation', 'negotiation', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OP', 'tenderCancellation', 'PN', 'PN', 'planning', 'empty', 'cancelPlan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'auctionPeriodEnd', 'EV', 'EV', 'auction', 'awarding', 'auctionPeriodEnd');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OP', 'buyerSigningAC', 'AC', 'AC', 'approvement', 'approved', 'buyerSigningAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'NP', 'verificationAC', 'AC', 'AC', 'signed', 'verification', 'verificationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'acRejection', 'AC', 'AC', 'verification', '', 'processAcRejection');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'verificationAC', 'AC', 'AC', 'signed', 'verification', 'verificationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OP', 'treasuryApprovingAC', 'AC', 'AC', 'verification', 'verified', 'treasuryApprovingAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'acRejection', 'AC', 'AC', 'verification', '', 'processAcRejection');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'confirmCan', 'EV', 'EV', 'awarding', 'awarding', 'confirmCan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'acClarification', 'AC', 'AC', 'verification', '', 'processAcClarification');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'supplierSigningAC', 'AC', 'AC', 'approved', 'signed', 'supplierSigningAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'createCan', 'EV', 'EV', 'awarding', 'awarding', 'createCan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_DA', 'treasuryApprovingAC', 'AC', 'AC', 'verification', 'verified', 'treasuryApprovingAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'NP', 'acRejection', 'AC', 'AC', 'verification', '', 'processAcRejection');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'DA', 'createAC', 'NP', 'NP', 'awarding', 'contractProject', 'createAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_DA', 'createAwardOp', 'NP', 'NP', 'awarding', 'awarding', 'createAward');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_DA', 'createAwardOp', 'NP', 'NP', 'negotiation', 'awarding', 'startAwardPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'updateBidDocs', 'EV', 'EV', 'awarding', 'awarding', 'updateBidDocs');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'acClarification', 'AC', 'AC', 'verification', '', 'processAcClarification');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'confirmCan', 'EV', 'EV', 'awarding', 'awarding', 'confirmCan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'createCN', 'EV', 'EV', 'clarification', 'clarification', 'createCN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OP', 'finalUpdateAC', 'AC', 'AC', 'issued', 'approvement', 'finalUpdateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'treasuryApprovingAC', 'AC', 'AC', 'verification', 'verified', 'treasuryApprovingAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'acRejection', 'AC', 'AC', 'verification', '', 'processAcRejection');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'startConsiderByAward', 'EV', 'EV', 'awarding', 'awarding', 'doAwardConsideration');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'updateCanDocs', 'EV', 'EV', 'awarding', 'awarding', 'updateCanDocs');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OP', 'supplierSigningAC', 'AC', 'AC', 'approved', 'signed', 'supplierSigningAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'updatePN', 'PN', 'PN', 'planning', 'planning', 'updatePN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'NP', 'updateAC', 'AC', 'AC', 'contractPreparation', 'contractPreparation', 'updateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'NP', 'updateAC', 'AC', 'AC', 'contractProject', 'contractPreparation', 'updateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'DA', 'updateAC', 'AC', 'AC', 'contractPreparation', 'contractPreparation', 'updateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'DA', 'updateAC', 'AC', 'AC', 'contractProject', 'contractPreparation', 'updateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'acClarification', 'AC', 'AC', 'verification', '', 'processAcClarification');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'acRejection', 'AC', 'AC', 'verification', '', 'processAcRejection');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'treasuryApprovingAC', 'AC', 'AC', 'verification', 'verified', 'treasuryApprovingAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'buyerSigningAC', 'AC', 'AC', 'approvement', 'approved', 'buyerSigningAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'finalUpdateAC', 'AC', 'AC', 'issued', 'approvement', 'finalUpdateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'updateCN', 'EV', 'EV', 'clarification', 'clarification', 'updateCN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'updateCN', 'PIN', 'EV', 'planned', 'clarification', 'createCNonPIN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'updateCN', 'PN', 'EV', 'planning', 'clarification', 'createCNonPN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'awardByBid', 'EV', 'EV', 'awarding', 'awarding', 'awardByBidEv');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'updateAC', 'AC', 'AC', 'contractPreparation', 'contractPreparation', 'updateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'updateAC', 'AC', 'AC', 'contractProject', 'contractPreparation', 'updateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'tenderCancellation', 'EV', 'EV', 'clarification', 'clarification', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'tenderCancellation', 'EV', 'EV', 'tendering', 'tendering', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'tenderCancellation', 'PN', 'PN', 'planning', 'empty', 'cancelPlan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_NP', 'acRejection', 'AC', 'AC', 'verification', '', 'processAcRejection');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'tenderPeriodEnd', 'EV', 'EV', 'tendering', 'awarding', 'tenderPeriodEndEv');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'bidWithdrawn', 'EV', 'EV', 'tendering', 'tendering', 'bidWithdrawn');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'finalUpdateAC', 'AC', 'AC', 'issued', 'approvement', 'finalUpdateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'NP', 'issuingAC', 'AC', 'AC', 'contractPreparation', 'issued', 'issuingAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'updateBidDocs', 'EV', 'EV', 'awarding', 'awarding', 'updateBidDocs');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'bidWithdrawn', 'EV', 'EV', 'tendering', 'tendering', 'bidWithdrawn');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_DA', 'updateCnOp', 'PN', 'NP', 'planning', 'negotiation', 'createNegotiationCnOnPn');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'activationAC', 'AC', 'AC', 'signedContract', 'execution', 'activationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'activationAC', 'AC', 'AC', 'verified', 'execution', 'activationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'answer', 'EV', 'EV', 'clarification', 'clarification', 'addAnswer');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'answer', 'EV', 'EV', 'suspended', 'clarification', 'addAnswer');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OP', 'treasuryApprovingAC', 'AC', 'AC', 'verification', 'verified', 'treasuryApprovingAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_DA', 'updateAC', 'AC', 'AC', 'contractPreparation', 'contractPreparation', 'updateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_DA', 'updateAC', 'AC', 'AC', 'contractProject', 'contractPreparation', 'updateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'issuingAC', 'AC', 'AC', 'contractPreparation', 'issued', 'issuingAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'answer', 'EV', 'EV', 'clarification', 'clarification', 'addAnswer');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'answer', 'EV', 'EV', 'suspended', 'clarification', 'addAnswer');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'tenderPeriodEndAuction', 'EV', 'EV', 'tendering', 'auction', 'tenderPeriodEndAuction');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'updatePN', 'PN', 'PN', 'planning', 'planning', 'updatePN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_DA', 'issuingAC', 'AC', 'AC', 'contractPreparation', 'issued', 'issuingAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'NP', 'updatePN', 'PN', 'PN', 'planning', 'planning', 'updatePN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OP', 'createPN', '', 'PN', '', 'planning', 'createPN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'createAC', 'EV', 'EV', 'awarding', 'contractProject', 'createAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'createCN', 'EV', 'EV', 'clarification', 'clarification', 'createCN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OP', 'updateAC', 'AC', 'AC', 'contractPreparation', 'contractPreparation', 'updateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OP', 'updateAC', 'AC', 'AC', 'contractProject', 'contractPreparation', 'updateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'tenderPeriodEndAuction', 'EV', 'EV', 'tendering', 'auction', 'tenderPeriodEndAuction');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OP', 'activationAC', 'AC', 'AC', 'signedContract', 'execution', 'activationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OP', 'activationAC', 'AC', 'AC', 'verified', 'execution', 'activationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'evaluateAward', 'EV', 'EV', 'awarding', 'awarding', 'evaluateAward');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_NP', 'updateCnOp', 'PN', 'NP', 'planning', 'negotiation', 'createNegotiationCnOnPn');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'createCan', 'EV', 'EV', 'awarding', 'awarding', 'createCan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OP', 'createProtocol', 'NP', 'NP', 'awarding', 'awarding', 'createProtocol');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'acRejection', 'AC', 'AC', 'verification', '', 'processAcRejection');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'awardByBid', 'EV', 'EV', 'awarding', 'awarding', 'awardByBidEv');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'startConsiderByAward', 'EV', 'EV', 'awarding', 'awarding', 'doAwardConsideration');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'updateBidDocs', 'EV', 'EV', 'awarding', 'awarding', 'updateBidDocs');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'activationAC', 'AC', 'AC', 'signedContract', 'execution', 'activationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'activationAC', 'AC', 'AC', 'verified', 'execution', 'activationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'createCN', 'EV', 'EV', 'clarification', 'clarification', 'createCN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OP', 'acClarification', 'AC', 'AC', 'verification', '', 'processAcClarification');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'updateAC', 'AC', 'AC', 'contractPreparation', 'contractPreparation', 'updateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'updateAC', 'AC', 'AC', 'contractProject', 'contractPreparation', 'updateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'finalUpdateAC', 'AC', 'AC', 'issued', 'approvement', 'finalUpdateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'buyerSigningAC', 'AC', 'AC', 'approvement', 'approved', 'buyerSigningAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'answer', 'EV', 'EV', 'clarification', 'clarification', 'addAnswer');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'answer', 'EV', 'EV', 'suspended', 'clarification', 'addAnswer');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OP', 'activationAC', 'AC', 'AC', 'signedContract', 'execution', 'activationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OP', 'activationAC', 'AC', 'AC', 'verified', 'execution', 'activationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'enquiry', 'EV', 'EV', 'clarification', 'clarification', 'createEnquiry');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'supplierSigningAC', 'AC', 'AC', 'approved', 'signed', 'supplierSigningAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OP', 'createPN', '', 'PN', '', 'planning', 'createPN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'buyerSigningAC', 'AC', 'AC', 'approvement', 'approved', 'buyerSigningAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_DA', 'tenderCancellation', 'NP', 'NP', 'awarding', 'awarding', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_DA', 'tenderCancellation', 'NP', 'NP', 'negotiation', 'negotiation', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_DA', 'tenderCancellation', 'PN', 'PN', 'planning', 'empty', 'cancelPlan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_DA', 'finalUpdateAC', 'AC', 'AC', 'issued', 'approvement', 'finalUpdateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'tenderCancellation', 'EV', 'EV', 'awarding', 'awarding', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'tenderCancellation', 'EV', 'EV', 'clarification', 'clarification', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'tenderCancellation', 'EV', 'EV', 'tendering', 'tendering', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'tenderCancellation', 'PN', 'PN', 'planning', 'empty', 'cancelPlan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OP', 'acClarification', 'AC', 'AC', 'verification', '', 'processAcClarification');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OP', 'updateAC', 'AC', 'AC', 'contractPreparation', 'contractPreparation', 'updateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OP', 'updateAC', 'AC', 'AC', 'contractProject', 'contractPreparation', 'updateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'cancelCan', 'EV', 'EV', 'awarding', 'awarding', 'cancelCan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_NP', 'createPN', '', 'PN', '', 'planning', 'createPN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'updateAC', 'AC', 'AC', 'contractPreparation', 'contractPreparation', 'updateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'updateAC', 'AC', 'AC', 'contractProject', 'contractPreparation', 'updateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'updateBidDocs', 'EV', 'EV', 'awarding', 'awarding', 'updateBidDocs');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'enquiryPeriodEnd', 'EV', 'EV', 'clarification', 'tendering', 'enquiryPeriodEnd');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'enquiryPeriodEnd', 'EV', 'EV', 'clarification', 'tendering', 'enquiryPeriodEnd');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OP', 'finalUpdateAC', 'AC', 'AC', 'issued', 'approvement', 'finalUpdateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_DA', 'verificationAC', 'AC', 'AC', 'signed', 'verification', 'verificationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'activationAC', 'AC', 'AC', 'signedContract', 'execution', 'activationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'activationAC', 'AC', 'AC', 'verified', 'execution', 'activationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_NP', 'finalUpdateAC', 'AC', 'AC', 'issued', 'approvement', 'finalUpdateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'evaluateAward', 'EV', 'EV', 'awarding', 'awarding', 'evaluateAward');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_NP', 'updateAC', 'AC', 'AC', 'contractPreparation', 'contractPreparation', 'updateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_NP', 'updateAC', 'AC', 'AC', 'contractProject', 'contractPreparation', 'updateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'auctionPeriodEnd', 'EV', 'EV', 'auction', 'awarding', 'auctionPeriodEnd');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'issuingAC', 'AC', 'AC', 'contractPreparation', 'issued', 'issuingAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_NP', 'treasuryApprovingAC', 'AC', 'AC', 'verification', 'verified', 'treasuryApprovingAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'submitTheBid', 'EV', 'EV', 'tendering', 'tendering', 'submitTheBid');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'awardByBid', 'EV', 'EV', 'awarding', 'awarding', 'awardByBidEv');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'NP', 'createAwardOp', 'NP', 'NP', 'awarding', 'awarding', 'createAward');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'NP', 'createAwardOp', 'NP', 'NP', 'negotiation', 'awarding', 'startAwardPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'startConsiderByAward', 'EV', 'EV', 'awarding', 'awarding', 'doAwardConsideration');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'enquiry', 'EV', 'EV', 'clarification', 'clarification', 'createEnquiry');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'createPN', 'PN', 'PN', 'planning', 'planning', 'createPN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_DA', 'supplierSigningAC', 'AC', 'AC', 'approved', 'signed', 'supplierSigningAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'updateCanDocs', 'EV', 'EV', 'awarding', 'awarding', 'updateCanDocs');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_NP', 'updatePN', 'PN', 'PN', 'planning', 'planning', 'updatePN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'DA', 'activationAC', 'AC', 'AC', 'signedContract', 'execution', 'activationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'DA', 'activationAC', 'AC', 'AC', 'verified', 'execution', 'activationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_NP', 'issuingAC', 'AC', 'AC', 'contractPreparation', 'issued', 'issuingAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'createPN', '', 'PN', '', 'planning', 'createPN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'bidWithdrawn', 'EV', 'EV', 'tendering', 'tendering', 'bidWithdrawn');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'confirmCan', 'EV', 'EV', 'awarding', 'awarding', 'confirmCan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'bidWithdrawn', 'EV', 'EV', 'tendering', 'tendering', 'bidWithdrawn');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_DA', 'acClarification', 'AC', 'AC', 'verification', '', 'processAcClarification');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'supplierSigningAC', 'AC', 'AC', 'approved', 'signed', 'supplierSigningAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'activationAC', 'AC', 'AC', 'signedContract', 'execution', 'activationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'activationAC', 'AC', 'AC', 'verified', 'execution', 'activationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'activationAC', 'AC', 'AC', 'signedContract', 'execution', 'activationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'activationAC', 'AC', 'AC', 'verified', 'execution', 'activationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'createCan', 'EV', 'EV', 'awarding', 'awarding', 'createCan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'tenderCancellation', 'EV', 'EV', 'awarding', 'awarding', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'tenderCancellation', 'EV', 'EV', 'clarification', 'clarification', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'tenderCancellation', 'EV', 'EV', 'tendering', 'tendering', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'tenderCancellation', 'PN', 'PN', 'planning', 'empty', 'cancelPlan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'updateAC', 'AC', 'AC', 'contractPreparation', 'contractPreparation', 'updateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'updateAC', 'AC', 'AC', 'contractProject', 'contractPreparation', 'updateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'startConsiderByAward', 'EV', 'EV', 'awarding', 'awarding', 'doAwardConsideration');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'NP', 'acClarification', 'AC', 'AC', 'verification', '', 'processAcClarification');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'auctionPeriodEnd', 'EV', 'EV', 'auction', 'awarding', 'auctionPeriodEnd');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OP', 'evaluateAward', 'NP', 'NP', 'awarding', 'awarding', 'evaluateAward');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'buyerSigningAC', 'AC', 'AC', 'approvement', 'approved', 'buyerSigningAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'createAC', 'EV', 'EV', 'awarding', 'contractProject', 'createAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OP', 'tenderCancellation', 'NP', 'NP', 'awarding', 'awarding', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OP', 'tenderCancellation', 'NP', 'NP', 'negotiation', 'negotiation', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OP', 'tenderCancellation', 'PN', 'PN', 'planning', 'empty', 'cancelPlan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'createPN', 'PN', 'PN', 'planning', 'planning', 'createPN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OP', 'buyerSigningAC', 'AC', 'AC', 'approvement', 'approved', 'buyerSigningAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OP', 'updatePN', 'PN', 'PN', 'planning', 'planning', 'updatePN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'createProtocol', 'EV', 'EV', 'awarding', 'awarding', 'createProtocol');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'activationAC', 'AC', 'AC', 'signedContract', 'execution', 'activationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'activationAC', 'AC', 'AC', 'verified', 'execution', 'activationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'updateCanDocs', 'EV', 'EV', 'awarding', 'awarding', 'updateCanDocs');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'DA', 'acClarification', 'AC', 'AC', 'verification', '', 'processAcClarification');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'awardByBid', 'EV', 'EV', 'awarding', 'awarding', 'awardByBidEv');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'createAC', 'EV', 'EV', 'awarding', 'contractProject', 'createAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'DA', 'createAwardOp', 'NP', 'NP', 'awarding', 'awarding', 'createAward');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'DA', 'createAwardOp', 'NP', 'NP', 'negotiation', 'awarding', 'startAwardPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'submitTheBid', 'EV', 'EV', 'tendering', 'tendering', 'submitTheBid');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'createPN', 'PN', 'PN', 'planning', 'planning', 'createPN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'DA', 'createPN', '', 'PN', '', 'planning', 'createPN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'buyerSigningAC', 'AC', 'AC', 'approvement', 'approved', 'buyerSigningAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'createProtocol', 'EV', 'EV', 'awarding', 'awarding', 'createProtocol');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'bidWithdrawn', 'EV', 'EV', 'tendering', 'tendering', 'bidWithdrawn');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'answer', 'EV', 'EV', 'clarification', 'clarification', 'addAnswer');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'answer', 'EV', 'EV', 'suspended', 'clarification', 'addAnswer');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'tenderCancellation', 'EV', 'EV', 'clarification', 'clarification', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'tenderCancellation', 'EV', 'EV', 'tendering', 'tendering', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'tenderCancellation', 'PN', 'PN', 'planning', 'empty', 'cancelPlan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'evaluateAward', 'EV', 'EV', 'awarding', 'awarding', 'evaluateAward');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'updateBidDocs', 'EV', 'EV', 'awarding', 'awarding', 'updateBidDocs');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_NP', 'activationAC', 'AC', 'AC', 'signed', 'execution', 'activationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_NP', 'activationAC', 'AC', 'AC', 'signedContract', 'execution', 'activationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_NP', 'activationAC', 'AC', 'AC', 'verified', 'execution', 'activationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'bidWithdrawn', 'EV', 'EV', 'tendering', 'tendering', 'bidWithdrawn');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'DA', 'acRejection', 'AC', 'AC', 'verification', '', 'processAcRejection');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'treasuryApprovingAC', 'AC', 'AC', 'verification', 'verified', 'treasuryApprovingAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OP', 'createAwardOp', 'NP', 'NP', 'awarding', 'awarding', 'createAward');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OP', 'createAwardOp', 'NP', 'NP', 'negotiation', 'awarding', 'startAwardPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'enquiry', 'EV', 'EV', 'clarification', 'clarification', 'createEnquiry');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'NP', 'tenderCancellation', 'NP', 'NP', 'awarding', 'awarding', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'NP', 'tenderCancellation', 'NP', 'NP', 'negotiation', 'negotiation', 'cancellationStandstillPeriod');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'NP', 'tenderCancellation', 'PN', 'PN', 'planning', 'empty', 'cancelPlan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'verificationAC', 'AC', 'AC', 'signed', 'verification', 'verificationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'updateAC', 'AC', 'AC', 'contractPreparation', 'contractPreparation', 'updateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'updateAC', 'AC', 'AC', 'contractProject', 'contractPreparation', 'updateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'DA', 'treasuryApprovingAC', 'AC', 'AC', 'verification', 'verified', 'treasuryApprovingAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'issuingAC', 'AC', 'AC', 'contractPreparation', 'issued', 'issuingAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'verificationAC', 'AC', 'AC', 'signed', 'verification', 'verificationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'enquiry', 'EV', 'EV', 'clarification', 'clarification', 'createEnquiry');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OP', 'createAC', 'NP', 'NP', 'awarding', 'contractProject', 'createAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'createPN', 'PN', 'PN', 'planning', 'planning', 'createPN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OP', 'acRejection', 'AC', 'AC', 'verification', '', 'processAcRejection');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'evaluateAward', 'EV', 'EV', 'awarding', 'awarding', 'evaluateAward');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'tenderPeriodEnd', 'EV', 'EV', 'tendering', 'awarding', 'tenderPeriodEndEv');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'evaluateAward', 'EV', 'EV', 'awarding', 'awarding', 'evaluateAward');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_DA', 'activationAC', 'AC', 'AC', 'signedContract', 'execution', 'activationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_DA', 'activationAC', 'AC', 'AC', 'verified', 'execution', 'activationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'createProtocol', 'EV', 'EV', 'awarding', 'awarding', 'createProtocol');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OP', 'acRejection', 'AC', 'AC', 'verification', '', 'processAcRejection');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'createCN', 'EV', 'EV', 'clarification', 'clarification', 'createCN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'createCan', 'EV', 'EV', 'awarding', 'awarding', 'createCan');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'createAC', 'EV', 'EV', 'awarding', 'contractProject', 'createAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'updateCN', 'EV', 'EV', 'clarification', 'clarification', 'updateCN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'updateCN', 'PIN', 'EV', 'planned', 'clarification', 'createCNonPIN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'updateCN', 'PN', 'EV', 'planning', 'clarification', 'createCNonPN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_NP', 'evaluateAward', 'NP', 'NP', 'awarding', 'awarding', 'evaluateAward');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'createProtocol', 'EV', 'EV', 'awarding', 'awarding', 'createProtocol');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_NP', 'supplierSigningAC', 'AC', 'AC', 'approved', 'signed', 'supplierSigningAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'DA', 'supplierSigningAC', 'AC', 'AC', 'approved', 'signed', 'supplierSigningAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'MV', 'finalUpdateAC', 'AC', 'AC', 'issued', 'approvement', 'finalUpdateAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'DA', 'issuingAC', 'AC', 'AC', 'contractPreparation', 'issued', 'issuingAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'NP', 'evaluateAward', 'NP', 'NP', 'awarding', 'awarding', 'evaluateAward');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OP', 'supplierSigningAC', 'AC', 'AC', 'approved', 'signed', 'supplierSigningAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'supplierSigningAC', 'AC', 'AC', 'approved', 'signed', 'supplierSigningAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OP', 'createAC', 'NP', 'NP', 'awarding', 'contractProject', 'createAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'NP', 'treasuryApprovingAC', 'AC', 'AC', 'verification', 'verified', 'treasuryApprovingAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OP', 'updateCnOp', 'PN', 'NP', 'planning', 'negotiation', 'createNegotiationCnOnPn');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OP', 'issuingAC', 'AC', 'AC', 'contractPreparation', 'issued', 'issuingAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_NP', 'verificationAC', 'AC', 'AC', 'signed', 'verification', 'verificationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'createPN', 'PN', 'PN', 'planning', 'planning', 'createPN');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OT', 'acClarification', 'AC', 'AC', 'verification', '', 'processAcClarification');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'treasuryApprovingAC', 'AC', 'AC', 'verification', 'verified', 'treasuryApprovingAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'OP', 'verificationAC', 'AC', 'AC', 'signed', 'verification', 'verificationAC');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'tenderPeriodEnd', 'EV', 'EV', 'tendering', 'awarding', 'tenderPeriodEndEv');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'SV', 'buyerSigningAC', 'AC', 'AC', 'approvement', 'approved', 'buyerSigningAC');


INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'cancelTender', 'EV', 'EV', 'clarification', 'clarification', 'tenderCancellation');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'cancelTender', 'EV', 'EV', 'tendering', 'tendering', 'tenderCancellation');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'cancelTender', 'EV', 'EV', 'awarding', 'awarding', 'tenderCancellation');

INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'cancelLot', 'EV', 'EV', 'clarification', 'clarification', 'lotCancellation');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'cancelLot', 'EV', 'EV', 'tendering', 'tendering', 'lotCancellation');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'cancelLot', 'EV', 'EV', 'awarding', 'awarding', 'lotCancellation');

INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_OT', 'declareNonConflictOfInterest', 'EV', 'EV', 'awarding', 'awarding', 'declareNonConflictOfInterest');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_SV', 'declareNonConflictOfInterest', 'EV', 'EV', 'awarding', 'awarding', 'declareNonConflictOfInterest');
INSERT INTO orchestrator.rules (country, pmd, process_definition_key, stage_from, stage_to, phase_from, phase_to, operation_type)
VALUES ('MD', 'TEST_MV', 'declareNonConflictOfInterest', 'EV', 'EV', 'awarding', 'awarding', 'declareNonConflictOfInterest');