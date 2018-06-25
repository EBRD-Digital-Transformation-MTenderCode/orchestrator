package com.procurement.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.domain.response.ResponseDto;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ProcessService {

    void startProcess(Params params, Map<String, Object> variables);

    void startProcessCheckRules(Params params);

    void startProcessError(Params params, String message);

    void terminateProcess(String processId, String message);

    void terminateProcessWithMessage(Params params, String processId, String message);

    JsonNode processResponse(ResponseEntity<ResponseDto> responseEntity,
                             Params params,
                             String processId,
                             String taskId,
                             JsonNode request);

    String getText(String fieldName, JsonNode jsonData, String processId);

    Boolean getBoolean(String fieldName, JsonNode jsonData, String processId);

    String getTenderPeriodEndDate(JsonNode jsonData, String processId);

    Params addAccessToParams(Params params, String entityType, String entityId, JsonNode responseData, String processId);

    Params addBidAccessToParams(Params params, JsonNode responseData, String processId);

    Params addAwardAccessToParams(Params params, JsonNode responseData, String processId);

    Params addContractAccessToParams(Params params, JsonNode responseData, String processId);

    JsonNode addTenderTenderPeriod(JsonNode jsonData, JsonNode periodData, String processId);

    JsonNode addTenderTenderPeriodStartDate(JsonNode jsonData, String startDate, String processId);

    JsonNode addTenderEnquiryPeriod(JsonNode jsonData, JsonNode periodData, String processId);

    JsonNode addTenderStatus(JsonNode jsonData, JsonNode statusData, String processId);

    JsonNode addLots(JsonNode jsonData, JsonNode lotsData, String processId);

    JsonNode addLotsAndItems(JsonNode jsonData, JsonNode data, String processId);

    JsonNode addAwardData(JsonNode jsonData, JsonNode awardData, String processId);

    JsonNode addAwards(JsonNode jsonData, JsonNode awardsData, String processId);

    JsonNode addCans(JsonNode jsonData, JsonNode cansData, String processId);

    JsonNode addContracts(JsonNode jsonData, JsonNode data, String processId);

    JsonNode getUnsuccessfulLots(JsonNode jsonData, String processId);

    JsonNode getTenderLots(JsonNode jsonData, String processId);

    JsonNode addUpdateBidsStatusData(JsonNode jsonData, JsonNode bidsData, String processId);

    JsonNode addBidsAndTenderPeriod(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode addBids(JsonNode jsonData, JsonNode responseData, String processId);

    Boolean isBidsEmpty(JsonNode responseData, String processId);

    Boolean isAwardsEmpty(JsonNode responseData, String processId);

    String getFsId(JsonNode jsonData, String processId);

    String getFsToken(JsonNode jsonData, String processId);

    String getAwardRelatedBid(JsonNode jsonData, String processId);

    String getLotId(JsonNode jsonData, String processId);

    String getAwardStatusDetails(JsonNode jsonData, String processId);

    JsonNode getNextAward(JsonNode jsonData, String processId);

    JsonNode addUpdatedBid(JsonNode jsonData, JsonNode bidData, String processId);

    JsonNode addUpdatedLot(JsonNode jsonData, JsonNode lotData, String processId);

    JsonNode getDocumentsOfTender(JsonNode jsonData, String processId);

    JsonNode setDocumentsOfTender(JsonNode jsonData, JsonNode documentsData, String processId);

    JsonNode getDocumentsOfAward(JsonNode jsonData, String processId);

    JsonNode setDocumentsOfAward(JsonNode jsonData, JsonNode documentsData, String processId);

    JsonNode getDocumentsOfBids(JsonNode jsonData, String processId);

    JsonNode setDocumentsOfBids(JsonNode jsonData, JsonNode documentsData, String processId);

    JsonNode addStandstillPeriod(JsonNode jsonData, String startDate, String endDate, String processId);

    JsonNode getCheckFs(JsonNode jsonData, String startDate, String processId);

    JsonNode setCheckFs(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode setAccessData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getAccessData(JsonNode jsonData, String processId);

    JsonNode setTender(JsonNode jsonData, JsonNode responseData, String processId);
}

