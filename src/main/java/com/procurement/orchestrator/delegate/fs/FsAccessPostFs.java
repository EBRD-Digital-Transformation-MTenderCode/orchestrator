package com.procurement.orchestrator.delegate.fs;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.constant.ResponseMessageType;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import com.procurement.orchestrator.rest.AccessRestClient;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Optional;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class FsAccessPostFs implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(FsAccessPostFs.class);

    private final AccessRestClient accessRestClient;

    private final OperationService operationService;

    private final JsonUtil jsonUtil;

    public FsAccessPostFs(final AccessRestClient accessRestClient,
                          final OperationService operationService,
                          final JsonUtil jsonUtil) {
        this.accessRestClient = accessRestClient;
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info("->Data preparation for E-Access.");
//        final String transactionId = execution.getProcessBusinessKey();
//        final Optional<OperationEntity> entityOptional = operationService.getOperationByStep(transactionId, 1);
//        if (entityOptional.isPresent()) {
//            LOG.info("->Send data to E-Access.");
//            final OperationEntity entity = entityOptional.get();
//            final ResponseDto response;
//            try {
//                final JsonNode requestDto = jsonUtil.toJsonNode(entity.getJsonData());
//                final ResponseEntity<ResponseDto> responseEntity = accessRestClient.postCreateFs(requestDto);
//                response = responseEntity.getBody();
//                LOG.info("->Get response: " + response.getData());
//            } catch (Exception e) {
//                LOG.error(e.getMessage());
//                throw new BpmnError("TR_EXCEPTION", ResponseMessageType.SERVICE_EXCEPTION.value());
//            }
//
//            final OperationValue operation = new OperationValue(
//                transactionId,
//                2,
//                "get FS release from access",
//                "e-access",
//                "e-notice",
//                entity.getProcessType(),
//                jsonUtil.toJson(response.getData()));
//
//            operationService.saveOperation(operation);
//        }
    }
}
