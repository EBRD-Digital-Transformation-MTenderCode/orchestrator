package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.model

import com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer.UpdateAwardProcess
import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class UpdateAwardProcessRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<UpdateAwardProcess.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/award/request_update_award_full.json")
    }

    @Test
    fun required1() {
        testingBindingAndMapping<UpdateAwardProcess.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/award/request_update_award_required_1.json")
    }

   @Test
    fun required2() {
        testingBindingAndMapping<UpdateAwardProcess.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/award/request_update_award_required_2.json")
    }

    @Test
    fun required3() {
        testingBindingAndMapping<UpdateAwardProcess.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/award/request_update_award_required_3.json")
    }
}
