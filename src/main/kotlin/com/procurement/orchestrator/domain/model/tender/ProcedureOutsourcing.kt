package com.procurement.orchestrator.domain.model.tender

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import java.io.Serializable

data class ProcedureOutsourcing(
    @field:JsonProperty("procedureOutsourced") @param:JsonProperty("procedureOutsourced") val procedureOutsourced: Boolean
) : ComplexObject<ProcedureOutsourcing>, Serializable {

    override fun updateBy(src: ProcedureOutsourcing): ProcedureOutsourcing =
        ProcedureOutsourcing(procedureOutsourced = src.procedureOutsourced)
}
