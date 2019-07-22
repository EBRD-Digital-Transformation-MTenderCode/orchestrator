package com.procurement.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Notification;
import com.procurement.orchestrator.domain.dto.command.CommandMessage;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService {

    Context addEnquiryOutcomeToContext(Context context, JsonNode responseData, String processId);

    Context addBidOutcomeToContext(Context context, JsonNode responseData, String processId);

    Context addAwardOutcomeToContext(Context context, JsonNode responseData, String processId);

    Context addOneAwardOutcomeToContext(final Context context, final JsonNode responseData, final String processId);

    Context addCanOutcomeToContext(Context context, JsonNode responseData, String processId);

    Context addContractOutcomeToContext(Context context, JsonNode responseData, String processId);

    Context addNoticeOutcomeToContext(Context context, JsonNode responseData, String processId);

    String getTenderUri(String cpId, String ocId);

    Notification getNotificationForPlatform(Context context);

    Notification getNotificationForPlatformCA(Context context);

    CommandMessage getCommandMessage(Enum command, Context context, JsonNode data);

    CommandMessage getAgentCommandMessage(Enum command, Context context, JsonNode data);
}


