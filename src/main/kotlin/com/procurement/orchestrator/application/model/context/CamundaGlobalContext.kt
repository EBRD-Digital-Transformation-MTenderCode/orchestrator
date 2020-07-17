package com.procurement.orchestrator.application.model.context

import com.procurement.orchestrator.application.model.context.container.PropertyContainer
import com.procurement.orchestrator.application.model.context.members.Errors
import com.procurement.orchestrator.application.model.context.members.Incident
import com.procurement.orchestrator.application.model.context.members.Outcomes
import com.procurement.orchestrator.application.model.context.members.ProcessInfo
import com.procurement.orchestrator.application.model.context.members.RequestInfo
import com.procurement.orchestrator.application.model.context.property.collectionPropertyDelegate
import com.procurement.orchestrator.application.model.context.property.nullablePropertyDelegate
import com.procurement.orchestrator.application.model.context.property.propertyDelegate
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.award.Awards
import com.procurement.orchestrator.domain.model.bid.Bids
import com.procurement.orchestrator.domain.model.contract.Contracts
import com.procurement.orchestrator.domain.model.invitation.Invitations
import com.procurement.orchestrator.domain.model.party.Parties
import com.procurement.orchestrator.domain.model.qualification.PreQualification
import com.procurement.orchestrator.domain.model.qualification.Qualifications
import com.procurement.orchestrator.domain.model.submission.Submissions
import com.procurement.orchestrator.domain.model.tender.Tender

class CamundaGlobalContext(propertyContainer: PropertyContainer) : GlobalContext {

    override var requestInfo: RequestInfo by propertyDelegate(propertyContainer)

    override var processInfo: ProcessInfo by propertyDelegate(propertyContainer)

    override var outcomes: Outcomes? by nullablePropertyDelegate(propertyContainer)

    override var errors: Errors? by nullablePropertyDelegate(propertyContainer)

    override var incident: Incident? by nullablePropertyDelegate(propertyContainer)

    override var tender: Tender? by nullablePropertyDelegate(propertyContainer)

    override var bids: Bids? by nullablePropertyDelegate(propertyContainer)

    override var awards: Awards by collectionPropertyDelegate(propertyContainer) { Awards() }

    override var parties: Parties by collectionPropertyDelegate(propertyContainer) { Parties() }

    override var contracts: Contracts by collectionPropertyDelegate(propertyContainer) { Contracts() }

    override var submissions: Submissions? by nullablePropertyDelegate(propertyContainer)

    override var preQualification: PreQualification? by nullablePropertyDelegate(propertyContainer)

    override var qualifications: Qualifications by collectionPropertyDelegate(propertyContainer) {Qualifications() }

    override var invitations: Invitations by collectionPropertyDelegate(propertyContainer) {Invitations() }
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
        override var submissions: Submissions? = this@serialize.submissions
        override var preQualification: PreQualification? = this@serialize.preQualification
        override var qualifications: Qualifications = this@serialize.qualifications
        override var invitations: Invitations = this@serialize.invitations
    }.let { context ->
        transform.trySerialization(context)
    }
