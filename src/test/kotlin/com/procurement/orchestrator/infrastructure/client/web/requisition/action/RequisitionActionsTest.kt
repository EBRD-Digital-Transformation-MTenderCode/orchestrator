package com.procurement.orchestrator.infrastructure.client.web.requisition.action

import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RequisitionActionsTest {

    @Nested
    inner class ValidatePcrData {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<ValidatePcrDataAction.Params>("json/client/requisition/validate_pcr_data_params_full.json")
            }
        }
    }

    @Nested
    inner class CheckLotsState {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckLotsStateAction.Params>("json/client/requisition/check_lots_state_params_full.json")
            }
        }
    }

}
