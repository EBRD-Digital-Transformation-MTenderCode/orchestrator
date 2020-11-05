package com.procurement.orchestrator.service.context;

import com.procurement.orchestrator.domain.Stage;


public class ContextProviderFactory {

    public static ContextProvider getContextProvider(Stage stage) {
        ContextProvider provider = null;
        switch (stage) {
            case PC:
                provider = ContextProviderByOcid.getInstance();
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
                provider = ContextProviderByCpid.getInstance();
                break;
        }
        return provider;
    }
}
