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
                testingBindingAndMapping<EnrichCountryAction.Response.Success>("json/client/mdm/enrich_country_response_full.json")
            }
        }
    }

    @Nested
    inner class EnrichRegionResponse {

        @Nested
        inner class Result {

            @Test
            fun fully() {
                testingBindingAndMapping<EnrichRegionAction.Response.Success>("json/client/mdm/enrich_region_response_full.json")
            }
        }
    }

    @Nested
    inner class EnrichLocalityResponse {

        @Nested
        inner class Result {

            @Test
            fun fully() {
                testingBindingAndMapping<EnrichLocalityAction.Response.Success>("json/client/mdm/enrich_locality_response_full.json")
            }
        }
    }

    @Nested
    inner class GetRequirementGroups {

        @Nested
        inner class Result {

            @Test
            fun fully() {
                testingBindingAndMapping<GetRequirementGroupsAction.Response.Success>("json/client/mdm/get_requirement_group_response_full.json")
            }
        }
    }

}
