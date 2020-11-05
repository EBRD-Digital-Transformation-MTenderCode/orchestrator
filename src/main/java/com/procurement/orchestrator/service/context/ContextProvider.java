package com.procurement.orchestrator.service.context;

import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Stage;
import com.procurement.orchestrator.service.RequestService;


public class ContextProvider {
    public static Context getContext(RequestService requestService, Stage stage, String authorization, String operationId, String cpid, String ocid, String token, String process) {
        Context context = null;
        switch (stage) {
            case PC:
                context = requestService.getContextForUpdateByOcid(authorization, operationId, cpid, ocid, token, process);
                break;
            case AC:
            case AP:
            case EI:
            case EV:
            case FE:
            case FS:
            case NP:
            case PN:
            case PQ:
            case PS:
            case TP:
            case PIN:
                context = requestService.getContextForUpdateByCpid(authorization, operationId, cpid, ocid, token, process);
                break;
        }
        return context;
    }
}
