package com.procurement.orchestrator.delegate.cn;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import com.procurement.orchestrator.rest.NoticeRestClient;
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
public class CnNoticePostCn implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CnNoticePostCn.class);

    private final NoticeRestClient noticeRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public CnNoticePostCn(final NoticeRestClient noticeRestClient,
                          final OperationService operationService,
                          final ProcessService processService,
                          final JsonUtil jsonUtil) {
        this.noticeRestClient = noticeRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
        final Optional<OperationStepEntity> entityOptional = operationService.getPreviousOperationStep(execution);
        if (entityOptional.isPresent()) {
            final OperationStepEntity entity = entityOptional.get();
            final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
            final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
            final String releaseDate = getReleaseDate(jsonData);
            try {
                final ResponseEntity<ResponseDto> responseEntity = noticeRestClient.createCn(
                        params.getCpid(),
                        "ps",
                        releaseDate,
                        jsonData
                );
                JsonNode responseData = jsonUtil.toJsonNode(responseEntity.getBody().getData());
                operationService.saveOperationStep(execution, entity, responseData);
            } catch (FeignException e) {
                LOG.error(e.getMessage(), e);
                processService.processHttpException(e.status(), e.getMessage(), execution.getProcessInstanceId());
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                processService.processHttpException(0, e.getMessage(), execution.getProcessInstanceId());
            }
        }
    }

    private String getReleaseDate(JsonNode jsonData) {
        final JsonNode tenderNode = jsonData.get("tender");
        final JsonNode tenderPeriodNode = tenderNode.get("tenderPeriod");
        return tenderPeriodNode.get("startDate").asText();
    }
}
