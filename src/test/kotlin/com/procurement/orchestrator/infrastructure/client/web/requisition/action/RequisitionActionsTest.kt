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
    inner class CheckTenderState {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckTenderStateAction.Params>("json/client/requisition/check_tender_state_params_full.json")
            }
        }
    }

    @Nested
    inner class FindProcurementMethodModalities {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<FindProcurementMethodModalitiesAction.Params>("json/client/requisition/find_procurement_method_modalities_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<FindProcurementMethodModalitiesAction.Result>("json/client/requisition/find_procurement_method_modalities_result_full.json")
            }
        }
    }

}
