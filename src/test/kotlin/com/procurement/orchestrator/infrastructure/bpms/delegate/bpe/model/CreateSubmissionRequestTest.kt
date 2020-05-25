package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.model

import com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer.CreateSubmission
import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class CreateSubmissionRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<CreateSubmission.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/request_create_submission_full.json")
    }

    @Test
    fun required1() {
        testingBindingAndMapping<CreateSubmission.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/request_create_submission_required_1.json")
    }

    @Test
    fun required2() {
        testingBindingAndMapping<CreateSubmission.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/request_create_submission_required_2.json")
    }

    @Test
    fun required3() {
        testingBindingAndMapping<CreateSubmission.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/request_create_submission_required_3.json")
    }
}
