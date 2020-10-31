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

    @Nested
    inner class GetCriteria {

        @Nested
        inner class Result {

            @Test
            fun fully() {
                testingBindingAndMapping<GetCriteriaAction.Response.Success>("json/client/mdm/get_criteria_response_full.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<GetCriteriaAction.Response.Success>("json/client/mdm/get_criteria_response_required_1.json")
            }

            @Test
            fun required_2() {
                testingBindingAndMapping<GetCriteriaAction.Response.Success>("json/client/mdm/get_criteria_response_required_2.json")
            }
        }
    }

    @Nested
    inner class GetRequirements {

        @Nested
        inner class Result {

            @Test
            fun fully() {
                testingBindingAndMapping<GetRequirementsAction.Response.Success>("json/client/mdm/get_requirements_response_full.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<GetRequirementsAction.Response.Success>("json/client/mdm/get_requirements_response_required_1.json")
            }

            @Test
            fun required_2() {
                testingBindingAndMapping<GetRequirementsAction.Response.Success>("json/client/mdm/get_requirements_response_required_2.json")
            }
        }
    }

    @Nested
    inner class EnrichClassificationResponse {

        @Nested
        inner class Result {

            @Test
            fun fully() {
                testingBindingAndMapping<EnrichClassificationsAction.Response.Success>("json/client/mdm/enrich_classification_response_full.json")
            }
        }
    }

    @Nested
    inner class EnrichUnitResponse {

        @Nested
        inner class Result {

            @Test
            fun fully() {
                testingBindingAndMapping<EnrichUnitsAction.Response.Success>("json/client/mdm/enrich_unit_response_full.json")
            }
        }
    }

}
