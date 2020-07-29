package com.procurement.orchestrator.infrastructure.client.web.auction.action

import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AuctionActionsTest {

    @Nested
    inner class ScheduleAuctions {

        @Nested
        inner class Data {
            @Test
            fun fully() {
                testingBindingAndMapping<ScheduleAuctionsAction.Data>("json/client/auction/schedule_auctions_data_full.json")
            }
        }

        @Nested
        inner class Context {
            @Test
            fun fully() {
                testingBindingAndMapping<ScheduleAuctionsAction.Context>("json/client/auction/schedule_auctions_context_full.json")
            }
        }

        @Nested
        inner class ResponseData {
            @Test
            fun fully() {
                testingBindingAndMapping<ScheduleAuctionsAction.ResponseData>("json/client/auction/schedule_auctions_response_data_full.json")
            }
        }
    }
}
