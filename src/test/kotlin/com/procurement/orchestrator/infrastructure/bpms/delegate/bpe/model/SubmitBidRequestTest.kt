package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.model

import com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer.SubmitBid
import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class SubmitBidRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<SubmitBid.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/bid/request_submit_bid_full.json")
    }

    @Test
    fun required1() {
        testingBindingAndMapping<SubmitBid.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/award/request_update_award_required_1.json")
    }

   @Test
    fun required2() {
        testingBindingAndMapping<SubmitBid.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/award/request_update_award_required_2.json")
    }

    @Test
    fun required3() {
        testingBindingAndMapping<SubmitBid.Request.Payload>("json/infrastructure/bpms/delegate/bpe/model/award/request_update_award_required_3.json")
    }
}
