package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.model

import com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer.StartSecondStage
import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class StartSecondStageProcessRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<StartSecondStage.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/secondstage/request_start_second_stage_full.json")
    }
}
