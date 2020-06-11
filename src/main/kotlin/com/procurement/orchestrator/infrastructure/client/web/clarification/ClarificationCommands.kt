package com.procurement.orchestrator.infrastructure.client.web.clarification

import com.procurement.orchestrator.infrastructure.client.web.clarification.action.FindEnquiryIdsAction
import com.procurement.orchestrator.infrastructure.client.web.clarification.action.GetEnquiryByIdsAction

object ClarificationCommands {

    object FindEnquiryIds : FindEnquiryIdsAction()

    object GetEnquiryByIds: GetEnquiryByIdsAction()
}
