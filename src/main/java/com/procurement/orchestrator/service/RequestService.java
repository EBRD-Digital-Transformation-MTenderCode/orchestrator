package com.procurement.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Rule;
import com.procurement.orchestrator.domain.entity.RequestEntity;
import org.springframework.stereotype.Service;

@Service
public interface RequestService {

    void validate(String operationId, JsonNode data);

    void saveRequest(String requestId, String operationId, Context context, JsonNode jsonData);

    RequestEntity getRequestById(String requestId, String processId);

    String getOwner(String authorization);

    void saveRequestAndCheckOperation(Context context, JsonNode jsonData);

    Context getContext(String cpId);

    Rule checkAndGetRule(Context prevContext, String processType);

    String getProcessType(String country, String pmd, String process);

    Rule getRule(String country, String pmd, String processType);

    Context getContextForCreate(String authorization,
                                String operationId,
                                String country,
                                String pmd,
                                String process,
                                boolean testMode);

    Context getContextForUpdateByCpid(String authorization,
                                      String operationId,
                                      String cpid,
                                      String ocid,
                                      String token,
                                      String process);

    Context getContextForUpdateByOcid(String authorization,
                                String operationId,
                                String cpid,
                                String ocid,
                                String token,
                                String process);

    Context getContextForContractUpdate(String authorization,
                                        String operationId,
                                        String cpid,
                                        String ocid,
                                        String token,
                                        String process);

    Context checkRulesAndProcessContext(Context prevContext, String processType, String requestId);

}
