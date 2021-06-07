package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.model

import com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer.CreateContractProcess
import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class CreateContractProcessRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<CreateContractProcess.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/contract/request_create_contract_full.json")
    }
}
