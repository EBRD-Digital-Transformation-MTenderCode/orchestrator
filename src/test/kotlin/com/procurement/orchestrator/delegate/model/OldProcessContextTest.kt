package com.procurement.orchestrator.delegate.model

import com.procurement.orchestrator.application.model.process.OldProcessContext
import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class OldProcessContextTest {

    @Test
    fun fully() {
        testingBindingAndMapping<OldProcessContext>("json/old_process_context.json")
    }
}
