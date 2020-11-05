package com.procurement.orchestrator.service.context;

import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.service.RequestService;

public interface ContextProvider {
    Context getContext(RequestService requestService, String authorization, String operationId, String cpid, String ocid, String token, String process);
}
