package com.procurement.orchestrator.infrastructure.client.web.qualification.action

import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class QualificationActionTest {

    @Nested
    inner class StartQualificationPeriod {
        @Nested
        inner class Params {
            @Test
            fun required() {
                testingBindingAndMapping<StartQualificationPeriodAction.Params>("json/client/qualification/start_qualification_period_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun required() {
                testingBindingAndMapping<StartQualificationPeriodAction.Result>("json/client/qualification/start_qualification_period_result_full.json")
            }
        }
    }

    @Nested
    inner class FindQualificationIds {

        @Nested
        inner class Params {
            @Test
            fun required() {
                testingBindingAndMapping<FindQualificationIdsAction.Params>("json/client/qualification/find_qualification_ids_params_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<FindQualificationIdsAction.Params>("json/client/qualification/find_qualification_ids_params_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<FindQualificationIdsAction.Params>("json/client/qualification/find_qualification_ids_params_2.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun required() {
                testingBindingAndMapping<FindQualificationIdsAction.Result>("json/client/qualification/find_qualification_ids_result_full.json")
            }
        }

        @Nested
        inner class CheckDeclaration {
            @Nested
            inner class Params {
                @Test
                fun required() {
                    testingBindingAndMapping<CheckDeclarationAction.Params>("json/client/qualification/check_declataion_params_full.json")
                }
            }
        }

        @Nested
        inner class DoDeclaration {
            @Nested
            inner class Params {
                @Test
                fun required() {
                    testingBindingAndMapping<DoDeclarationAction.Params>("json/client/qualification/do_declaration_params_full.json")
                }
            }

            @Nested
            inner class Result {
                @Test
                fun required() {
                    testingBindingAndMapping<DoDeclarationAction.Result>("json/client/qualification/do_declaration_result_full.json")
                }
            }
        }

        @Nested
        inner class FindRequirementResponseByIds {
            @Nested
            inner class Params {
                @Test
                fun required() {
                    testingBindingAndMapping<FindRequirementResponseByIdsAction.Params>("json/client/qualification/find_requirement_response_by_id_params_full.json")
                }
            }

            @Nested
            inner class Result {
                @Test
                fun required() {
                    testingBindingAndMapping<FindRequirementResponseByIdsAction.Result>("json/client/qualification/find_requirement_response_by_id_result_full.json")
                }
            }
        }
    }

    @Nested
    inner class CreateQualification {
        @Nested
        inner class Params {
            @Test
            fun required() {
                testingBindingAndMapping<CreateQualificationAction.Params>("json/client/qualification/create_qualification_params_full.json")
            }
            @Test
            fun required1() {
                testingBindingAndMapping<CreateQualificationAction.Params>("json/client/qualification/create_qualification_params_1.json")
            }
            @Test
            fun required2() {
                testingBindingAndMapping<CreateQualificationAction.Params>("json/client/qualification/create_qualification_params_2.json")
            }
        }
        @Nested
        inner class Result {
            @Test
            fun required() {
                testingBindingAndMapping<CreateQualificationAction.Result>("json/client/qualification/create_qualification_result_full.json")
            }
            @Test
            fun required1() {
                testingBindingAndMapping<CreateQualificationAction.Result>("json/client/qualification/create_qualification_result_1.json")
            }
        }
    }

    @Nested
    inner class RankQualifications {
        @Nested
        inner class Params {

            @Test
            fun required() {
                testingBindingAndMapping<RankQualificationsAction.Params>("json/client/qualification/rank_qualifications_params_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<RankQualificationsAction.Params>("json/client/qualification/rank_qualifications_params_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<RankQualificationsAction.Params>("json/client/qualification/rank_qualifications_params_2.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun required() {
                testingBindingAndMapping<RankQualificationsAction.Result>("json/client/qualification/rank_qualifications_result_full.json")
            }
        }
    }

    @Nested
    inner class CheckQualificationState {

        @Nested
        inner class Params {
            @Test
            fun required() {
                testingBindingAndMapping<CheckQualificationStateAction.Params>("json/client/qualification/check_qualification_state_params_full.json")
            }
        }

    }

    @Nested
    inner class CheckAccessToQualification {

        @Nested
        inner class Params {
            @Test
            fun full() {
                testingBindingAndMapping<CheckAccessToQualificationAction.Params>("json/client/qualification/check_access_to_qualification_params_full.json")
            }
        }
    }

    @Nested
    inner class DoConsideration {
        @Nested
        inner class Params {

            @Test
            fun required() {
                testingBindingAndMapping<DoConsiderationAction.Params>("json/client/qualification/do_consideration_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun required() {
                testingBindingAndMapping<DoConsiderationAction.Result>("json/client/qualification/do_consideration_result_full.json")
            }
        }
    }
}
