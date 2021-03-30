package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.model

import com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer.CreateRfqRequest
import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class CreateRfqRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<CreateRfqRequest.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/rfq/request_create_rfq_full.json")
    }

    @Test
    fun required1() {
        testingBindingAndMapping<CreateRfqRequest.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/rfq/request_create_rfq_required_1.json")
    }
}
