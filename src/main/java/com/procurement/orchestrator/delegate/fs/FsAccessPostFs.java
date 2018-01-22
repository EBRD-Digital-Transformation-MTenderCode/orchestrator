package com.procurement.orchestrator.delegate.fs;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import com.procurement.orchestrator.rest.AccessRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import feign.FeignException;
import java.util.Optional;
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

    private final ProcessService processService;

    private final JsonUtil jsonUtil;


    public FsAccessPostFs(final AccessRestClient accessRestClient,
                          final OperationService operationService,
                          final ProcessService processService,
                          final JsonUtil jsonUtil) {
        this.accessRestClient = accessRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info("->Data preparation for E-Access.");
//        final String txId = execution.getProcessBusinessKey();
//        final Optional<OperationStepEntity> entityOptional = operationService.getLastOperation(txId);
//        if (entityOptional.isPresent()) {
//            LOG.info("->Send data to E-Access.");
//            final OperationStepEntity entity = entityOptional.get();
//            final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
//            final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
//            try {
//                final ResponseEntity<ResponseDto> responseEntity = accessRestClient.postCreateFs(
//                        params.getCountry(),
//                        params.getPmd(),
//                        "fs",
//                        params.getOwner(),
//                        jsonData);
//                JsonNode responseData = jsonUtil.toJsonNode(responseEntity.getBody().getData());
//                operationService.processResponse(entity, addTokenToParams(params, responseData), responseData);
//            } catch (FeignException e) {
//                LOG.error(e.getMessage());
//                processService.processHttpException(e.status(), e.getMessage(), execution.getProcessInstanceId());
//            } catch (Exception e) {
//                LOG.error(e.getMessage());
//                processService.processHttpException(0, e.getMessage(), execution.getProcessInstanceId());
//            }
//        }
    }

    private Params addTokenToParams(Params params, JsonNode responseData) {
        if (responseData.get("token") != null) {
            params.setToken(responseData.get("token").asText());
        }
        return params;
    }
}
