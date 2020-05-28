package com.procurement.orchestrator.application.model.context

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.context.members.Errors
import com.procurement.orchestrator.application.model.context.members.Incident
import com.procurement.orchestrator.application.model.context.members.Outcomes
import com.procurement.orchestrator.application.model.context.members.ProcessInfo
import com.procurement.orchestrator.application.model.context.members.RequestInfo
import com.procurement.orchestrator.domain.model.award.Awards
import com.procurement.orchestrator.domain.model.bid.Bids
import com.procurement.orchestrator.domain.model.contract.Contracts
import com.procurement.orchestrator.domain.model.party.Parties
import com.procurement.orchestrator.domain.model.qualification.PreQualification
import com.procurement.orchestrator.domain.model.qualification.Qualifications
import com.procurement.orchestrator.domain.model.submission.Submissions
import com.procurement.orchestrator.domain.model.tender.Tender

interface GlobalContext {

    @get:JsonProperty("requestInfo")
    var requestInfo: RequestInfo

    @get:JsonProperty("processInfo")
    var processInfo: ProcessInfo

    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("outcomes")
    var outcomes: Outcomes?

    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("errors")
    var errors: Errors?

    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("incident")
    var incident: Incident?

    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("tender")
    var tender: Tender?

    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("bids")
    var bids: Bids?

    @get:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @get:JsonProperty("awards")
    var awards: Awards

    @get:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @get:JsonProperty("parties")
    var parties: Parties

    @get:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @get:JsonProperty("contracts")
    var contracts: Contracts

    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("submissions")
    var submissions: Submissions?

    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("preQualification")
    var preQualification: PreQualification?

    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("qualifications")
    var qualifications: Qualifications?
}
