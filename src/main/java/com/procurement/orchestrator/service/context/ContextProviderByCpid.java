package com.procurement.orchestrator.service.context;

import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.service.RequestService;

public class ContextProviderByCpid implements ContextProvider {

    private static class GeneralContextProviderHolder {
        public static final ContextProviderByCpid instance = new ContextProviderByCpid();
    }

    public static ContextProviderByCpid getInstance() {
        return GeneralContextProviderHolder.instance;
    }

    @Override
    public Context getContext(RequestService requestService, String authorization, String operationId, String cpid, String ocid, String token, String process) {
        return requestService.getContextForUpdate(authorization, operationId, cpid, ocid, token, process);
    }
}
