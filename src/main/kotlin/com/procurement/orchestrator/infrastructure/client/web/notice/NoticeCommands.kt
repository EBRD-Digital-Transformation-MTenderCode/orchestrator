package com.procurement.orchestrator.infrastructure.client.web.notice

import com.procurement.orchestrator.infrastructure.client.web.notice.action.CreateRecordAction
import com.procurement.orchestrator.infrastructure.client.web.notice.action.UpdateRecordAction

object NoticeCommands {

    object CreateRecord : CreateRecordAction()
    object UpdateRecord : UpdateRecordAction()
}
