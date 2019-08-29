package com.procurement.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface ProcessService {

    void startProcess(Context context, Map<String, Object> variables);

    void terminateProcess(String processId, String message);

    JsonNode getCommandMessage(Enum command, Context context, JsonNode data);

    JsonNode processResponse(ResponseEntity<ResponseDto> responseEntity,
                             Context context,
                             String processId,
                             String taskId,
                             JsonNode request);

    String getText(String fieldName, JsonNode jsonData, String processId);

    Boolean getBoolean(String fieldName, JsonNode jsonData, String processId);

    String getString(String fieldName, JsonNode jsonData, String processId);

    void setEnquiryPeriodStartDate(JsonNode jsonData, String startDate, String processId);

    void setTenderPeriodStartDate(JsonNode jsonData, String startDate, String processId);

    JsonNode setCheckEnquiryPeriod(JsonNode jsonData, JsonNode periodData, String processId);

    JsonNode getCheckTenderPeriod(JsonNode jsonData, String processId);

    JsonNode setCheckTenderPeriod(JsonNode jsonData, JsonNode periodData, String processId);

    String getTenderPeriodEndDate(JsonNode jsonData, String processId);

    String getEnquiryPeriodEndDate(JsonNode jsonData, String processId);

    JsonNode getTenderPeriod(JsonNode jsonData, String processId);

    JsonNode getEnquiryPeriod(JsonNode jsonData, String processId);

    JsonNode addTenderTenderPeriod(JsonNode jsonData, JsonNode periodData, String processId);

    JsonNode addTenderEnquiryPeriod(JsonNode jsonData, JsonNode periodData, String processId);

    JsonNode addEnquiryWithAnswer(JsonNode jsonData, JsonNode periodData, String processId);

    JsonNode addTenderStatus(JsonNode jsonData, JsonNode statusData, String processId);

    JsonNode addTenderUnsuspendData(JsonNode jsonData, JsonNode statusData, String processId);

    JsonNode getLots(JsonNode jsonData, String processId);

    JsonNode addLots(JsonNode jsonData, JsonNode lotsData, String processId);

    JsonNode addAccessFinalStatusesData(JsonNode jsonData, JsonNode lotsData, String processId);

    JsonNode addLotsUnsuccessful(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode addLotsAndAwardCriteria(JsonNode jsonData, JsonNode lotsData, String processId);

    JsonNode addAwardData(JsonNode jsonData, JsonNode awardData, String processId);

    JsonNode addOneAwardData(final JsonNode jsonData, final JsonNode awardData, final String processId);

    JsonNode addAwards(JsonNode jsonData, JsonNode awardsData, String processId);

    JsonNode addCan(JsonNode jsonData, JsonNode cansData, String processId);

    JsonNode getUnsuccessfulLots(JsonNode jsonData, String processId);

    JsonNode getTenderLots(JsonNode jsonData, String processId);

    JsonNode addUpdateBidsStatusData(JsonNode jsonData, JsonNode bidsData, String processId);

    JsonNode addBidsAndTenderPeriod(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode addBids(JsonNode jsonData, JsonNode responseData, String processId);

    Boolean isBidsEmpty(JsonNode responseData, String processId);

    JsonNode addUpdatedBid(JsonNode jsonData, JsonNode bidData, String processId);

    JsonNode addUpdatedLot(JsonNode jsonData, JsonNode lotData, String processId);

    JsonNode getDocumentsOfTender(JsonNode jsonData, String processId);

    JsonNode getDocumentsOfAmendmentsOfTender(final JsonNode jsonData, final String processId);

    JsonNode setDocumentsOfTender(JsonNode jsonData, JsonNode documentsData, String processId);

    JsonNode getDocumentsOfAward(JsonNode jsonData, String processId);

    JsonNode setDocumentsOfAward(JsonNode jsonData, JsonNode documentsData, String processId);

    JsonNode getDocumentsOfAcAward(JsonNode jsonData, String processId);

    JsonNode setDocumentsOfAcAward(JsonNode jsonData, JsonNode documentsData, String processId);

    JsonNode getDocumentsOfBids(JsonNode jsonData, String processId);

    JsonNode getDocumentsOfBid(JsonNode jsonData, String processId);

    JsonNode setDocumentsOfBids(JsonNode jsonData, JsonNode documentsData, String processId);

    JsonNode setDocumentsOfBid(JsonNode jsonData, JsonNode documentsData, String processId);

    JsonNode getDocumentsOfContractUpdate(JsonNode jsonData, String processId);

    JsonNode getDocumentsOfContract(JsonNode jsonData, String processId);

    JsonNode setDocumentsOfContract(JsonNode jsonData, JsonNode documentsData, String processId);

    JsonNode getDocumentsOfContractAward(JsonNode jsonData, String processId);

    JsonNode getDocumentsOfContractPersones(JsonNode jsonData, String processId);

    JsonNode getDocumentsOfCan(JsonNode jsonData, String processId);

    JsonNode getDocumentsOfCanStorageValidate(JsonNode jsonData, String processId);

    JsonNode getDocumentsOfCancelCanValidation(JsonNode jsonData, String processId);

    JsonNode getDocumentsOfCancelCanOpen(JsonNode jsonData, String processId);

    JsonNode setDocumentsOfCancelCanOpen(JsonNode jsonData, JsonNode documentsData, String processId);

    JsonNode setDocumentsOfCan(JsonNode jsonData, JsonNode documentsData, String processId);

    JsonNode setDocumentsOfContractAward(JsonNode jsonData, JsonNode documentsData, String processId);

    JsonNode setDocumentsOfContractPersones(JsonNode jsonData, JsonNode documentsData, String processId);

    JsonNode getDocumentsOfConsideredBid(JsonNode jsonData, String processId);

    JsonNode setDocumentsOfConsideredBid(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getDocumentsOfConfirmationResponse(JsonNode jsonData, String processId);

    JsonNode addStandstillPeriod(JsonNode jsonData, String startDate, String endDate, String processId);

    JsonNode setAccessData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getAccessData(JsonNode jsonData, String processId);

    JsonNode setTender(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getEiData(JsonNode jsonData, String processId);

    JsonNode setEiData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getFsData(JsonNode jsonData, String processId);

    JsonNode setFsData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getTenderData(Boolean itemsAdd, JsonNode jsonData, String processId);

    JsonNode setTenderData(Boolean itemsAdd, JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getContractData(JsonNode jsonData, String processId);

    JsonNode setContractData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getPlanning(JsonNode jsonData, String processId);

    JsonNode getCheckItems(JsonNode jsonData, String processId);

    JsonNode setCheckItems(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getCheckFs(JsonNode jsonData, String startDate, String processId);

    JsonNode setCheckFs(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getBidTenderersData(JsonNode jsonData, String processId);

    JsonNode setBidTenderersData(JsonNode jsonData, JsonNode responseData, String processId);

    String getEnquiryId(JsonNode jsonData, String processId);

    JsonNode getEnquiryAuthor(JsonNode jsonData, String processId);

    JsonNode setEnquiryAuthor(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getEnquiryRelatedLot(JsonNode jsonData, String processId);

    JsonNode getAuctionData(JsonNode prevData, String processId);

    JsonNode setAuctionData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getAuctionStartData(JsonNode prevData, String processId);

    JsonNode setAuctionStartData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getAuctionLaunchData(JsonNode jsonData, String processId);

    JsonNode setAuctionEndData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode addContractTerm(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode addActualBudgetSource(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getContractAwardValue(JsonNode jsonData, String processId);

    JsonNode getDataForGetTerms(JsonNode jsonData, String processId);

    JsonNode getAgreedMetrics(JsonNode jsonData, String processId);

    JsonNode setAgreedMetrics(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode setContractUpdateData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode setContractIssuedStatusDetails(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode setContractFinalUpdateData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getCheckBs(JsonNode jsonData, String processId);

    JsonNode setCheckBs(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getDataForBidUpdateStatus(JsonNode jsonData, String processId);

    JsonNode getConsideredBidId(JsonNode jsonData, String processId);

    JsonNode setConsideredBid(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode setRelatedBidsId(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getRelatedBidsData(JsonNode jsonData, String processId);

    JsonNode getTreasuryValidationData(JsonNode jsonData, Context context, String processId);

    JsonNode setCompleteLotsData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode setAwardPeriod(JsonNode jsonData, JsonNode responseData, String processId);

    //JsonNode getCan(JsonNode jsonData, String processId);

    JsonNode addCancelCan(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getAwards(JsonNode jsonData, String processId);

    JsonNode getLotId(JsonNode jsonData, String processId);

    JsonNode addLot(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode addInitialAwardsStatuses(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode addLotId(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode addCans(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode addContractedTender(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode addCreateAcData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode addEvaluationFinalData(JsonNode jsonData, JsonNode responseData, String processId);
}

