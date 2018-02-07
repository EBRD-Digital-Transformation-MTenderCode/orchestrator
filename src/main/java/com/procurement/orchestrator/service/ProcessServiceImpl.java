package com.procurement.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.dto.ResponseDetailsDto;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import com.procurement.orchestrator.kafka.MessageProducer;
import com.procurement.orchestrator.kafka.dto.PlatformMessage;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ProcessServiceImpl implements ProcessService {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessServiceImpl.class);

    private final RuntimeService runtimeService;

    private final MessageProducer messageProducer;

    private final OperationService operationService;

    private final JsonUtil jsonUtil;

    public ProcessServiceImpl(final RuntimeService runtimeService,
                              final MessageProducer messageProducer,
                              final OperationService operationService,
                              final JsonUtil jsonUtil) {
        this.operationService = operationService;
        this.runtimeService = runtimeService;
        this.messageProducer = messageProducer;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public ProcessInstance startProcess(final String processType, final String operationId) {
        return runtimeService.startProcessInstanceByKey(processType, operationId);
    }

    @Override
    public ProcessInstance startProcess(String processType, String operationId, Map<String, Object> variables) {
        return runtimeService.startProcessInstanceByKey(processType, operationId, variables);
    }

    @Override
    public JsonNode processResponse(final ResponseEntity<ResponseDto> responseEntity,
                                    final String processId,
                                    final String operationId,
                                    final String taskId) {
        if (responseEntity.getBody().getSuccess()) {
            return jsonUtil.toJsonNode(responseEntity.getBody().getData());
        } else {
            operationService.saveOperationException(processId, taskId, jsonUtil.toJsonNode(responseEntity.getBody()));
            processError(responseEntity.getBody().getDetails(), processId, operationId);
            return null;
        }
    }


    @Override
    public void processError(List<ResponseDetailsDto> details, String processId, String operationId) {
        final String errorMessage = Optional.ofNullable(details).map(d -> jsonUtil.toJson(details)).orElse("");
        LOG.info("Error in process Id: " + processId + "; message: " + errorMessage);
        messageProducer.sendToPlatform(new PlatformMessage(operationId, errorMessage));
        terminateProcess(processId);
    }

    @Override
    public void processError(String error, String processId, String operationId) {
        LOG.info("Error in process Id: " + processId + "; message: " + error);
        messageProducer.sendToPlatform(new PlatformMessage(operationId, error));
        terminateProcess(processId);
    }

    @Override
    public void processException(final String error,
                                 final String processId) {
        try {
            LOG.info("Exception in process Id: " + processId + "; message: " + error);
            runtimeService.suspendProcessInstanceById(processId);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void terminateProcess(String processId) {
        runtimeService.deleteProcessInstance(processId, "Removal of the backward process.");
    }


    @Override
    public String getValue(String fieldName, JsonNode responseData, String processId, String operationId) {
        try {
            return responseData.get(fieldName).asText();
        } catch (Exception e) {
            processError(e.getMessage(), processId, operationId);
        }
        return null;
    }


}
