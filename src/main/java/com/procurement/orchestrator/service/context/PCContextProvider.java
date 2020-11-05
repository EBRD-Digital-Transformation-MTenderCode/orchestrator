package com.procurement.orchestrator.service.context;

import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.service.RequestService;

public class PCContextProvider implements ContextProvider {

    private static class PCContextProviderHolder {
        public static final PCContextProvider instance = new PCContextProvider();
    }

    public static PCContextProvider getInstance() {
        return PCContextProviderHolder.instance;
    }

    @Override
    public Context getContext(RequestService requestService, String authorization, String operationId, String cpid, String ocid, String token, String process) {
        return requestService.getContextForUpdateByOcid(authorization, operationId, cpid, ocid, token, process);
    }
}