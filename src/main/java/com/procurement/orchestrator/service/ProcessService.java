package com.procurement.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import java.util.Map;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ProcessService {

    ProcessInstance startProcess(String processType, String operationId, Map<String, Object> variables);

    void terminateProcess(String processId, String message);

    JsonNode processResponse(ResponseEntity<ResponseDto> responseEntity,
                             String processId,
                             String operationId,
                             String taskId);

    String getText(String fieldName, JsonNode jsonData, String processId);

    Boolean getBoolean(String fieldName, JsonNode jsonData, String processId);

    String getTenderPeriodEndDate(JsonNode jsonData, String processId);

    JsonNode addTenderPeriodStartDate(JsonNode jsonData, String startDate, String processId);

    JsonNode addTenderTenderPeriod(JsonNode jsonData, JsonNode periodData, String processId);

    JsonNode addTenderEnquiryPeriod(JsonNode jsonData, JsonNode periodData, String processId);

    JsonNode addTenderStatus(JsonNode jsonData, JsonNode statusData, String processId);

    JsonNode addLots(JsonNode jsonData, JsonNode lotsData, String processId);

    JsonNode addAwardData(JsonNode jsonData, JsonNode awardData, String processId);

    JsonNode getUnsuccessfulLots(JsonNode jsonData, String processId);

    JsonNode addUpdateBidsStatusData(JsonNode jsonData, JsonNode bidsData, String processId);

    JsonNode addUpdateLotsStatusData(JsonNode jsonData, JsonNode lotsData, String processId);

    String getAwardRelatedBid(JsonNode jsonData, String processId);

    String getAwardStatus(JsonNode jsonData, String processId);

    JsonNode addUpdatedBid(JsonNode jsonData, JsonNode bidData, String processId);

    JsonNode getDocuments(JsonNode jsonData, String processId, String operationId);

    JsonNode setDatePublished(JsonNode jsonData, String startDate, String processId, String operationId);

}

