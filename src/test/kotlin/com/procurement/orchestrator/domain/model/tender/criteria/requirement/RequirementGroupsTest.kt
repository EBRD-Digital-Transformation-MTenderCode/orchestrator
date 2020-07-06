package com.procurement.orchestrator.domain.model.tender.criteria.requirement

import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class RequirementGroupsTest {

    @Test
    fun fully() {
        testingBindingAndMapping<RequirementGroups>("json/domain/model/tender/criteria/requirement/requirement_groups_fully.json")
    }
}
