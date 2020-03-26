package com.procurement.orchestrator.infrastructure.client.web.notice.action

import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class NoticeActionsTest {

    @Nested
    inner class CreateRecord {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateRecordAction.Params>("json/client/notice/create_record_params_full.json")
            }
        }
    }

    @Nested
    inner class UpdateRecord {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<UpdateRecordAction.Params>("json/client/notice/update_record_params_full.json")
            }
        }
    }
}
