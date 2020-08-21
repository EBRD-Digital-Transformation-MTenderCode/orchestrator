package com.procurement.orchestrator.infrastructure.client.web.qualification.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.ProcurementMethodDetails
import com.procurement.orchestrator.domain.model.address.country.CountryId
import com.procurement.orchestrator.domain.model.qualification.QualificationId
import com.procurement.orchestrator.infrastructure.model.Version

abstract class CheckQualificationStateAction : ProceduralAction<CheckQualificationStateAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "checkQualificationState"

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("qualificationId") @param:JsonProperty("qualificationId") val qualificationId: QualificationId,
        @field:JsonProperty("country") @param:JsonProperty("country") val country: CountryId,
        @field:JsonProperty("pmd") @param:JsonProperty("pmd") val pmd: ProcurementMethodDetails,
        @field:JsonProperty("operationType") @param:JsonProperty("operationType") val operationType: OperationTypeProcess
    )
}
