package com.procurement.orchestrator.infrastructure.model.dto.chronograph

import com.procurement.orchestrator.infrastructure.bpms.model.chronograph.ChronographMetadata
import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class ChronographMetadataTest {
    @Test
    fun fully() {
        testingBindingAndMapping<ChronographMetadata>("json/infrastructure/model/dto/chronograph/metadata_chronograph_full.json")
    }
}