package com.procurement.orchestrator.service.context;

import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.service.RequestService;

public class GeneralContextProvider implements ContextProvider {

    private static class GeneralContextProviderHolder {
        public static final GeneralContextProvider instance = new GeneralContextProvider();
    }

    public static GeneralContextProvider getInstance() {
        return GeneralContextProviderHolder.instance;
    }

    @Override
    public Context getContext(RequestService requestService, String authorization, String operationId, String cpid, String ocid, String token, String process) {
        return requestService.getContextForUpdate(authorization, operationId, cpid, ocid, token, process);
    }
}
