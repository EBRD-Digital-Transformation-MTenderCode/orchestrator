package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.model

import com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer.DivideLotProcess
import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class DivideLotProcessRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<DivideLotProcess.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/divide/lot/divide_lot_request_full.json")
    }

    @Test
    fun required1() {
        testingBindingAndMapping<DivideLotProcess.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/divide/lot/divide_lot_request_required_1.json")
    }

    @Test
    fun required2() {
        testingBindingAndMapping<DivideLotProcess.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/divide/lot/divide_lot_request_required_2.json")
    }
}
