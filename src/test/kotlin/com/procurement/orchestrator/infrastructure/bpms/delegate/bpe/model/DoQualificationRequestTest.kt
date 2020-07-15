package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.model

import com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer.DoQualification
import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class DoQualificationRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<DoQualification.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/qualification/request_do_qualification_full.json")
    }

    @Test
    fun required1() {
        testingBindingAndMapping<DoQualification.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/qualification/request_do_qualification_required_1.json")
    }

    @Test
    fun required2() {
        testingBindingAndMapping<DoQualification.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/qualification/request_do_qualification_required_2.json")
    }

}
