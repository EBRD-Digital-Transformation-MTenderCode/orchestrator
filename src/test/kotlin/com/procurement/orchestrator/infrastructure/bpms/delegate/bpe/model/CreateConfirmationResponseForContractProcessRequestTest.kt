package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.model

import com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer.CreateConfirmationResponseForContractProcess
import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class CreateConfirmationResponseForContractProcessRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<CreateConfirmationResponseForContractProcess.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/contract/request_create_confirmation_response_for_contract_process_full.json")
    }

    @Test
    fun required1() {
        testingBindingAndMapping<CreateConfirmationResponseForContractProcess.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/contract/request_create_confirmation_response_for_contract_process_required_1.json")
    }

    @Test
    fun required2() {
        testingBindingAndMapping<CreateConfirmationResponseForContractProcess.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/contract/request_create_confirmation_response_for_contract_process_required_2.json")
    }
}
