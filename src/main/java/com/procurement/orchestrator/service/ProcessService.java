package com.procurement.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.domain.response.ResponseDto;
import java.util.Map;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ProcessService {

    void startProcess(String processType, String operationId, Map<String, Object> variables);

    void terminateProcess(String processId, String message);

    JsonNode processResponse(ResponseEntity<ResponseDto> responseEntity,
                             String processId,
                             String operationId,
                             String taskId);

    String getText(String fieldName, JsonNode jsonData, String processId);

    Boolean getBoolean(String fieldName, JsonNode jsonData, String processId);

    String getTenderPeriodEndDate(JsonNode jsonData, String processId);

    Params addAccessToParams(Params params, String entityType, String entityId, JsonNode responseData, String
            processId);

    Params addBidAccessToParams(Params params, JsonNode responseData, String processId);

    Params addAwardAccessToParams(Params params, JsonNode responseData, String processId);

    JsonNode addTenderTenderPeriod(JsonNode jsonData, JsonNode periodData, String processId);

    JsonNode addTenderEnquiryPeriod(JsonNode jsonData, JsonNode periodData, String processId);

    JsonNode addTenderStatus(JsonNode jsonData, JsonNode statusData, String processId);

    JsonNode addLots(JsonNode jsonData, JsonNode lotsData, String processId);

    JsonNode addAwardData(JsonNode jsonData, JsonNode awardData, String processId);

    JsonNode getUnsuccessfulLots(JsonNode jsonData, String processId);

    JsonNode addUpdateBidsStatusData(JsonNode jsonData, JsonNode bidsData, String processId);

    JsonNode addBids(JsonNode jsonData, JsonNode lotsData, String processId);

    String getAwardRelatedBid(JsonNode jsonData, String processId);

    String getAwardStatusDetails(JsonNode jsonData, String processId);

    JsonNode addUpdatedBid(JsonNode jsonData, JsonNode bidData, String processId);

    JsonNode getDocuments(JsonNode jsonData, String processId, String operationId);

    JsonNode setDatePublished(JsonNode jsonData, String startDate, String processId, String operationId);

    JsonNode addStandstillPeriod(JsonNode jsonData, String startDate, String endDate, String processId);

}

