package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AccessActionsTest {

    @Nested
    inner class CheckAccessToTender {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckAccessToTenderAction.Params>("json/client/access/check_access_to_tender_params_full.json")
            }
        }
    }

    @Nested
    inner class CheckPersonsStructure {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckPersonsStructureAction.Params>("json/client/access/check_persons_structure_params_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<CheckPersonsStructureAction.Params>("json/client/access/check_persons_structure_params_required_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<CheckPersonsStructureAction.Params>("json/client/access/check_persons_structure_params_required_2.json")
            }
        }
    }

    @Nested
    inner class GetLotStateByIds {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetLotStateByIdsAction.Params>("json/client/access/get_lot_state_by_ids_action_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetLotStateByIdsAction.Result>("json/client/access/get_lot_state_by_ids_action_result_full.json")
            }
        }
    }

    @Nested
    inner class GetLotIdsActionTest {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetLotIdsAction.Params>("json/client/access/get_lot_ids_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetLotIdsAction.Result>("json/client/access/get_lot_ids_result_full.json")
            }
        }
    }
}
