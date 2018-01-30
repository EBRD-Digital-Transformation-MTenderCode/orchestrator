package com.procurement.orchestrator.delegate.cn.bid;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.delegate.cn.create.CnAccessCreateCn;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import com.procurement.orchestrator.rest.SubmissionRestClient;
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
public class CnSubmissionCreateBid implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CnAccessCreateCn.class);

    private final SubmissionRestClient submissionRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public CnSubmissionCreateBid(final SubmissionRestClient submissionRestClient,
                                 final OperationService operationService,
                                 final ProcessService processService,
                                 final JsonUtil jsonUtil) {
        this.submissionRestClient = submissionRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
//        final Optional<OperationStepEntity> entityOptional = operationService.getPreviousOperationStep(execution);
//        if (entityOptional.isPresent()) {
//            final OperationStepEntity entity = entityOptional.get();
//            final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
//            try {
//                final ResponseEntity<ResponseDto> responseEntity = submissionRestClient.createBid(
//                        params.getOcid(),
//                        "cn",
//                        params.getOwner(),
//                        jsonUtil.toJsonNode(entity.getJsonData()));
//                JsonNode responseData = jsonUtil.toJsonNode(responseEntity.getBody().getData());
//                operationService.saveOperationStep(
//                        execution,
//                        entity,
//                        addTokenToParams(params, responseData),
//                        responseData);
//            } catch (FeignException e) {
//                LOG.error(e.getMessage(), e);
//                processService.processException(e.status(), e.getMessage(), execution.getProcessInstanceId());
//            } catch (Exception e) {
//                LOG.error(e.getMessage(), e);
//                processService.processException(0, e.getMessage(), execution.getProcessInstanceId());
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
