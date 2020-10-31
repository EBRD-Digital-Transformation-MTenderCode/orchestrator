package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.model

import com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer.CreatePreAwardCatalogRequest
import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class CreatePreAwardCatalogRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<CreatePreAwardCatalogRequest.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/pcr/request_create_pcr_full.json")
    }

    @Test
    fun required1() {
        testingBindingAndMapping<CreatePreAwardCatalogRequest.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/pcr/request_create_pcr_required_1.json")
    }

    @Test
    fun required2() {
        testingBindingAndMapping<CreatePreAwardCatalogRequest.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/pcr/request_create_pcr_required_2.json")
    }
}
