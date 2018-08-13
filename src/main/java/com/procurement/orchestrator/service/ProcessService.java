package com.procurement.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.CommandMessage;
import com.procurement.orchestrator.domain.dto.CommandType;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ProcessService {

    void startProcess(Context context, Map<String, Object> variables);

    void startProcessCheckRules(Context context);

    void startProcessError(Context context, String message);

    void terminateProcess(String processId, String message);

    void terminateProcessWithMessage(Context context, String processId, String message);

    CommandMessage getCommandMessage(CommandType commandType, Context context, JsonNode data);

    JsonNode processResponse(ResponseEntity<ResponseDto> responseEntity,
                             Context context,
                             String processId,
                             String taskId,
                             JsonNode request);

    String getText(String fieldName, JsonNode jsonData, String processId);

    Boolean getBoolean(String fieldName, JsonNode jsonData, String processId);

    String getTenderPeriodEndDate(JsonNode jsonData, String processId);

    Context addAccessToContext(Context context, String entityType, String entityId, JsonNode responseData, String processId);

    Context addBidAccessToContext(Context context, JsonNode responseData, String processId);

    Context addAwardAccessToContext(Context context, JsonNode responseData, String processId);

    Context addContractAccessToContext(Context context, JsonNode responseData, String processId);

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

    JsonNode setAccessData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getAccessData(JsonNode jsonData, String processId);

    JsonNode setTender(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getEiData(JsonNode jsonData, String processId);

    JsonNode setEiData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getFsData(JsonNode jsonData, String processId);

    JsonNode setFsData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getTenderData(JsonNode jsonData, String processId);

    JsonNode setTenderData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getCheckItems(JsonNode jsonData, String processId);

    JsonNode setCheckItems(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getCheckFs(JsonNode jsonData, String startDate, String processId);

    JsonNode setCheckFs(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getBidTenderersData(JsonNode jsonData, String processId);

    JsonNode setBidTenderersData(JsonNode jsonData, JsonNode responseData, String processId);
}

