package com.procurement.orchestrator.domain.model.tender.criteria.requirement

import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class RequirementsTest {

    @Test
    fun fully() {
        testingBindingAndMapping<Requirements>("json/domain/model/tender/criteria/requirement/requirement_fully.json")
    }
}
