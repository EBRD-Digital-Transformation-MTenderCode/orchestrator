package com.procurement.orchestrator.infrastructure.client.web.mdm.action

import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MdmActionsTest {

    @Nested
    inner class EnrichCountryResponse {

        @Nested
        inner class Result {

            @Test
            fun fully() {
                testingBindingAndMapping<EnrichCountryAction.Result>("json/client/mdm/enrich_country_response_full.json")
            }
        }
    }

    @Nested
    inner class EnrichRegionResponse {

        @Nested
        inner class Result {

            @Test
            fun fully() {
                testingBindingAndMapping<EnrichRegionAction.Result>("json/client/mdm/enrich_region_response_full.json")
            }
        }
    }

    @Nested
    inner class EnrichLocalityResponse {

        @Nested
        inner class Result {

            @Test
            fun fully() {
                testingBindingAndMapping<EnrichLocalityAction.Result>("json/client/mdm/enrich_locality_response_full.json")
            }
        }
    }


}
