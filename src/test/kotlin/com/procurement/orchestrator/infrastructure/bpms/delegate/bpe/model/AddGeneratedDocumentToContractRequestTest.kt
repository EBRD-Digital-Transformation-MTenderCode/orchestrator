package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.model

import com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer.AddGeneratedDocumentToContract
import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class AddGeneratedDocumentToContractRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<AddGeneratedDocumentToContract.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/contract/request_add_generated_document_to_contract_full.json")
    }
}
