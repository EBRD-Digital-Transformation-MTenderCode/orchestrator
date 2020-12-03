package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.model

import com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer.CreateAwardProcess
import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class CreateAwardProcessRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<CreateAwardProcess.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/award/request_create_award_full.json")
    }

    @Test
    fun required1() {
        testingBindingAndMapping<CreateAwardProcess.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/award/request_create_requires_1.json")
    }

   @Test
    fun required2() {
        testingBindingAndMapping<CreateAwardProcess.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/award/request_create_requires_2.json")
    }

    @Test
    fun required3() {
        testingBindingAndMapping<CreateAwardProcess.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/award/request_create_requires_3.json")
    }
}
