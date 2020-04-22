package com.procurement.orchestrator.application.model.context

import com.procurement.orchestrator.application.model.context.container.PropertyContainer
import com.procurement.orchestrator.application.model.context.members.Awards
import com.procurement.orchestrator.application.model.context.members.Contracts
import com.procurement.orchestrator.application.model.context.members.Errors
import com.procurement.orchestrator.application.model.context.members.Incident
import com.procurement.orchestrator.application.model.context.members.Outcomes
import com.procurement.orchestrator.application.model.context.members.Parties
import com.procurement.orchestrator.application.model.context.members.ProcessInfo
import com.procurement.orchestrator.application.model.context.members.RequestInfo
import com.procurement.orchestrator.application.model.context.property.collectionPropertyDelegate
import com.procurement.orchestrator.application.model.context.property.nullablePropertyDelegate
import com.procurement.orchestrator.application.model.context.property.propertyDelegate
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.award.Award
import com.procurement.orchestrator.domain.model.bid.Bids
import com.procurement.orchestrator.domain.model.contract.Contract
import com.procurement.orchestrator.domain.model.party.Party
import com.procurement.orchestrator.domain.model.tender.Tender

class CamundaGlobalContext(propertyContainer: PropertyContainer) : GlobalContext {

    override var requestInfo: RequestInfo by propertyDelegate(propertyContainer)

    override var processInfo: ProcessInfo by propertyDelegate(propertyContainer)

    override var outcomes: Outcomes? by nullablePropertyDelegate(propertyContainer)

    override var errors: Errors? by nullablePropertyDelegate(propertyContainer)

    override var incident: Incident? by nullablePropertyDelegate(propertyContainer)

    override var tender: Tender? by nullablePropertyDelegate(propertyContainer)

    override var bids: Bids? by nullablePropertyDelegate(propertyContainer)

    override var awards: Awards by collectionPropertyDelegate(propertyContainer) { Awards(emptyList<Award>()) }

    override var parties: Parties by collectionPropertyDelegate(propertyContainer) { Parties(emptyList<Party>()) }

    override var contracts: Contracts by collectionPropertyDelegate(propertyContainer) { Contracts(emptyList<Contract>()) }
}

fun CamundaGlobalContext.serialize(transform: Transform): Result<String, Fail.Incident.Transform.Serialization> =
    object : GlobalContext {
        override var requestInfo: RequestInfo = this@serialize.requestInfo
        override var processInfo: ProcessInfo = this@serialize.processInfo
        override var outcomes: Outcomes? = this@serialize.outcomes
        override var errors: Errors? = this@serialize.errors
        override var incident: Incident? = this@serialize.incident
        override var tender: Tender? = this@serialize.tender
        override var bids: Bids? = this@serialize.bids
        override var awards: Awards = this@serialize.awards
        override var parties: Parties = this@serialize.parties
        override var contracts: Contracts = this@serialize.contracts
    }.let { context ->
        transform.trySerialization(context)
    }
