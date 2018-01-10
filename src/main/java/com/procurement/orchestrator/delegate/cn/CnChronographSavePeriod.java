package com.procurement.orchestrator.delegate.cn;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.cassandra.model.OperationEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.rest.SubmissionRestClient;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.time.LocalDateTime;
import java.util.Optional;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CnChronographSavePeriod implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CnChronographSavePeriod.class);

    private final SubmissionRestClient submissionRestClient;

    private final OperationService operationService;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;

    public CnChronographSavePeriod(final SubmissionRestClient submissionRestClient,
                                   final OperationService operationService,
                                   final JsonUtil jsonUtil,
                                   final DateUtil dateUtil) {
        this.submissionRestClient = submissionRestClient;
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info("->Data preparation for Chronograph.");
        final String txId = execution.getProcessBusinessKey();
        final Optional<OperationEntity> entityOptional = operationService.getLastOperation(txId);
        if (entityOptional.isPresent()) {
            LOG.info("->Send data to Chronograph.");
//            final OperationEntity entity = entityOptional.get();
//            final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
//            final LocalDateTime startDate = dateUtil.localDateTimeNowUTC();
//            final LocalDateTime endDate = getPeriodEndDate(entity);
//            try {
//                final ResponseEntity<ResponseDto> responseEntity = submissionRestClient.postCheckPeriod(
//                        params.getCountry(),
//                        params.getPmd(),
//                        "ps",
//                        dateUtil.format(startDate),
//                        dateUtil.format(endDate));
//                Map<String, Boolean> data = (HashMap) responseEntity.getBody().getData();
//                LOG.info("->Get response: " + data);
//                if (!data.get("period")) {
//                    throw new BpmnError("TR_EXCEPTION", ResponseMessageType.PERIOD_EXCEPTION.value());
//                }
//            } catch (Exception e) {
//                LOG.error(e.getMessage());
//                throw new BpmnError("TR_EXCEPTION", ResponseMessageType.SERVICE_EXCEPTION.value());
//            }
//            operationService.saveOperation(addPeriodStartDate(entity, dateUtil.format(startDate)));
        }
    }
}
