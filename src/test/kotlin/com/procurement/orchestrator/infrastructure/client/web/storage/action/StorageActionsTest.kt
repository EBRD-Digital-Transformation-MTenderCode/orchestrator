package com.procurement.orchestrator.infrastructure.client.web.storage.action

import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StorageActionsTest {

    @Nested
    inner class CheckRegistration {

        @Nested
        inner class Params {

            @Test
            fun fully() {
                testingBindingAndMapping<CheckRegistrationAction.Params>("json/client/storage/check_registration_params_full.json")
            }
        }
    }

    @Nested
    inner class OpenAccess {

        @Nested
        inner class Params {

            @Test
            fun fully() {
                testingBindingAndMapping<OpenAccessAction.Params>("json/client/storage/open_access_params_full.json")
            }
        }

        @Nested
        inner class Result {

            @Test
            fun fully() {
                testingBindingAndMapping<OpenAccessAction.Result>("json/client/storage/open_access_result_full.json")
            }
        }
    }
}
