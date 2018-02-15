package com.procurement.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.domain.dto.ResponseDetailsDto;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import java.util.List;
import java.util.Map;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ProcessService {


    ProcessInstance startProcess(String processType, String operationId);

    ProcessInstance startProcess(String processType, String operationId, Map<String, Object> variables);

    JsonNode processResponse(ResponseEntity<ResponseDto> responseEntity,
                             String processId,
                             String operationId,
                             String taskId);

    void processException(String error, String processId);

    void processError(List<ResponseDetailsDto> details, String processId, String operationId);

    void processError(String error, String processId, String operationId);

    void terminateProcess(String processId, String message);

    String getText(String fieldName, JsonNode responseData, String processId);

    Boolean getBoolean(String fieldName, JsonNode responseData, String processId);

    String getTenderPeriodEndDate(JsonNode jsonData, String processId, String operationId);

    JsonNode addTenderPeriodStartDate(JsonNode jsonData, String startDate, String processId);

    JsonNode addTenderTenderPeriod(JsonNode jsonData, JsonNode periodData, String processId);

    JsonNode addTenderEnquiryPeriod(JsonNode jsonData, JsonNode periodData, String processId);

    JsonNode addTenderStatus(JsonNode jsonData, JsonNode statusData, String processId);

}

