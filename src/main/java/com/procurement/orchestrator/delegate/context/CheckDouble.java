package com.procurement.orchestrator.delegate.context;

import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.service.RequestService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CheckDouble implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CheckDouble.class);

    private final OperationService operationService;

    private final ProcessService processService;

    private final RequestService requestService;

    private final JsonUtil jsonUtil;


    public CheckDouble(final RequestService requestService,
                       final OperationService operationService,
                       final ProcessService processService,
                       final JsonUtil jsonUtil) {
        this.requestService = requestService;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
//        LOG.info(execution.getCurrentActivityId());
//        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
//        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
//        final String checkId = context.getOcid() + context.getProcessType();
//        final String processId = execution.getProcessInstanceId();
//        if (!operationService.saveCheckIfNotExist(checkId, entity)) {
//            processService.terminateProcess(processId, "process: " + context.getProcessType()
//                    + " by ocid:" + context.getOcid() + " already launched.");
//        }
    }
}
