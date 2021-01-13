package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe

import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.GlobalContext
import com.procurement.orchestrator.application.model.context.extension.getAmendmentsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getAwardsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getBidsDetailIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getDetailsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getRequirementResponseIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.tryGetBids
import com.procurement.orchestrator.application.model.context.extension.tryGetElectronicAuctions
import com.procurement.orchestrator.application.model.context.extension.tryGetSubmissions
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asOption
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.functional.bind
import com.procurement.orchestrator.domain.model.amendment.Amendment
import com.procurement.orchestrator.domain.model.amendment.AmendmentId
import com.procurement.orchestrator.domain.model.amendment.Amendments
import com.procurement.orchestrator.domain.model.award.Awards
import com.procurement.orchestrator.domain.model.bid.Bid
import com.procurement.orchestrator.domain.model.bid.BidsDetails
import com.procurement.orchestrator.domain.model.candidate.Candidates
import com.procurement.orchestrator.domain.model.organization.Organization
import com.procurement.orchestrator.domain.model.organization.Organizations
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunction
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctionId
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctions
import com.procurement.orchestrator.domain.model.person.Person
import com.procurement.orchestrator.domain.model.person.Persons
import com.procurement.orchestrator.domain.model.qualification.Qualifications
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponse
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponses
import com.procurement.orchestrator.domain.model.requirement.response.evidence.Evidence
import com.procurement.orchestrator.domain.model.requirement.response.evidence.EvidenceId
import com.procurement.orchestrator.domain.model.requirement.response.evidence.Evidences
import com.procurement.orchestrator.domain.model.submission.Details
import com.procurement.orchestrator.domain.model.submission.Submission
import com.procurement.orchestrator.domain.model.tender.auction.AuctionId
import com.procurement.orchestrator.domain.model.tender.auction.ElectronicAuctions
import com.procurement.orchestrator.domain.model.tender.auction.ElectronicAuctionsDetails
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractInternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.springframework.stereotype.Component
import java.io.Serializable

@Component
class BpeCreateIdsDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractInternalDelegate<BpeCreateIdsDelegate.Parameters, List<BpeCreateIdsDelegate.Ids>>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        private const val PARAMETER_NAME_LOCATION = "location"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val location = parameterContainer.getListString(PARAMETER_NAME_LOCATION)
            .orForwardFail { fail -> return fail }
            .map {
                Location.orNull(it)
                    ?: return Result.failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = PARAMETER_NAME_LOCATION,
                            expectedValues = Location.allowedElements.keysAsStrings(),
                            actualValue = it
                        )
                    )
            }
        return Parameters(location = location)
            .asSuccess()
    }

    override suspend fun execute(
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Option<List<Ids>>, Fail.Incident> = parameters.location
        .map { location ->
            when (location) {
                Location.AUCTION -> generatePermanentAuctionsIds(context)
                Location.AWARD_REQUIREMENT_RESPONSE -> generatePermanentAwardRequirementResponsesIds(context)
                Location.BID_BUSINESS_FUNCTION -> generatePermanentBidBusinessFunctionIds(context)
                Location.BID_EVIDENCE -> generatePermanentBidEvidenceIds(context)
                Location.BID_REQUIREMENT_RESPONSE -> generatePermanentBidsRequirementResponseIds(context)
                Location.QUALIFICATION_REQUIREMENT_RESPONSE -> generatePermanentQualificationsRequirementResponseIds(
                    context
                )
                Location.SUBMISSION_BUSINESS_FUNCTION -> generatePermanentSubmissionBusinessFunctionIds(context)
                Location.SUBMISSION_EVIDENCE -> generatePermanentSubmissionEvidenceIds(context)
                Location.SUBMISSION_REQUIREMENT_RESPONSE -> generatePermanentSubmissionsRequirementResponseIds(context)
                Location.TENDER_AMENDMENT -> generatePermanentTenderAmendmentsIds(context)
            }
                .orForwardFail { fail -> return fail }
        }
        .asOption()
        .asSuccess()

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        data: List<Ids>
    ): MaybeFail<Fail.Incident> {

        data.forEach { permanentIds ->
            when (permanentIds) {
                is Ids.AwardRequirementResponses -> updateAwardRequirementResponsesIds(context, permanentIds)
                is Ids.BidsBusinessFunctions -> updateBidBusinessFunctionIds(context, permanentIds)
                is Ids.BidsEvidences -> updateBidEvidenceIds(context, permanentIds)
                is Ids.BidsRequirementResponses -> updateBidRequirementResponseIds(context, permanentIds)
                is Ids.QualificationsRequirementResponses -> updateQualificationRequirementResponseIds(
                    context,
                    permanentIds
                )
                is Ids.SubmissionBusinessFunctions -> updateSubmissionsBusinessFunctionIds(context, permanentIds)
                is Ids.SubmissionEvidence -> updateSubmissionEvidenceIds(context, permanentIds)
                is Ids.SubmissionsRequirementResponses -> updateSubmissionsRequirementResponseIds(context, permanentIds)
                is Ids.TenderAmendments -> updateTenderAmendmentsIds(context, permanentIds)
                is Ids.TenderAuctions -> updateTenderAuctionsIds(context, permanentIds)
            }
        }

        return MaybeFail.none()
    }

    private fun generatePermanentAwardRequirementResponsesIds(context: GlobalContext): Result<Ids, Fail.Incident> =
        context.getAwardsIfNotEmpty()
            .orForwardFail { fail -> return fail }
            .flatMap { award ->
                award.getRequirementResponseIfNotEmpty()
                    .orForwardFail { fail -> return fail }
            }
            .asSequence()
            .map { requirementResponse ->
                val temporal = requirementResponse.id
                val permanent = RequirementResponseId.generate()
                temporal to permanent
            }
            .toMap()
            .let {
                success(Ids.AwardRequirementResponses(it))
            }

    private fun generatePermanentBidBusinessFunctionIds(context: GlobalContext): Result<Ids, Fail.Incident> =
        context.tryGetBids()
            .orForwardFail { fail -> return fail }
            .getBidsDetailIfNotEmpty()
            .orForwardFail { fail -> return fail }
            .asSequence()
            .flatMap { bid -> bid.tenderers.asSequence() }
            .flatMap { tenderer -> tenderer.persons.asSequence() }
            .flatMap { person -> person.businessFunctions.asSequence() }
            .map { businessFunction ->
                val temporal = businessFunction.id
                val permanent = BusinessFunctionId.generate()
                temporal to permanent
            }

            .toMap()
            .let {
                success(Ids.BidsBusinessFunctions(it))
            }

    private fun generatePermanentBidEvidenceIds(context: GlobalContext): Result<Ids, Fail.Incident> =
        context.tryGetBids()
            .orForwardFail { fail -> return fail }
            .getBidsDetailIfNotEmpty()
            .orForwardFail { fail -> return fail }
            .asSequence()
            .flatMap { bid -> bid.requirementResponses.asSequence() }
            .flatMap { requirementResponse -> requirementResponse.evidences.asSequence() }
            .map { evidence ->
                val temporal = evidence.id
                val permanent = EvidenceId.generate()
                temporal to permanent
            }
            .toMap()
            .let {
                success(Ids.BidsEvidences(it))
            }

    private fun generatePermanentSubmissionBusinessFunctionIds(context: GlobalContext): Result<Ids, Fail.Incident> =
        context.tryGetSubmissions()
            .orForwardFail { fail -> return fail }
            .getDetailsIfNotEmpty()
            .orForwardFail { fail -> return fail }
            .asSequence()
            .flatMap { submission -> submission.candidates.asSequence() }
            .flatMap { candidate -> candidate.persons.asSequence() }
            .flatMap { person -> person.businessFunctions.asSequence() }
            .map { businessFunction ->
                val temporal = businessFunction.id
                val permanent = BusinessFunctionId.generate()
                temporal to permanent
            }

            .toMap()
            .let {
                success(Ids.SubmissionBusinessFunctions(it))
            }

    private fun generatePermanentSubmissionEvidenceIds(context: GlobalContext): Result<Ids, Fail.Incident> =
        context.tryGetSubmissions()
            .orForwardFail { fail -> return fail }
            .getDetailsIfNotEmpty()
            .orForwardFail { fail -> return fail }
            .asSequence()
            .flatMap { submission -> submission.requirementResponses.asSequence() }
            .flatMap { requirementResponse -> requirementResponse.evidences.asSequence() }
            .map { evidence ->
                val temporal = evidence.id
                val permanent = EvidenceId.generate()
                temporal to permanent
            }
            .toMap()
            .let {
                success(Ids.SubmissionEvidence(it))
            }

    private fun generatePermanentTenderAmendmentsIds(context: GlobalContext): Result<Ids, Fail.Incident> =
        context.tryGetTender()
            .orForwardFail { fail -> return fail }
            .getAmendmentsIfNotEmpty()
            .orForwardFail { fail -> return fail }
            .asSequence()
            .map { amendment ->
                val temporal = amendment.id
                val permanent = AmendmentId.generate()
                temporal to permanent
            }
            .toMap()
            .let {
                success(Ids.TenderAmendments(it))
            }

    private fun generatePermanentSubmissionsRequirementResponseIds(context: GlobalContext): Result<Ids, Fail.Incident> =
        context.tryGetSubmissions()
            .orForwardFail { fail -> return fail }
            .getDetailsIfNotEmpty()
            .orForwardFail { fail -> return fail }
            .flatMap { it.requirementResponses }
            .asSequence()
            .map { requirementResponse ->
                val temporal = requirementResponse.id
                val permanent = RequirementResponseId.generate()
                temporal to permanent
            }
            .toMap()
            .let { Ids.SubmissionsRequirementResponses(it) }
            .asSuccess()

    private fun generatePermanentQualificationsRequirementResponseIds(context: GlobalContext): Result<Ids, Fail.Incident> =
        context.qualifications
            .flatMap { it.requirementResponses }
            .asSequence()
            .map { requirementResponse ->
                val temporal = requirementResponse.id
                val permanent = RequirementResponseId.generate()
                temporal to permanent
            }
            .toMap()
            .let { Ids.QualificationsRequirementResponses(it) }
            .asSuccess()

    private fun generatePermanentBidsRequirementResponseIds(context: GlobalContext): Result<Ids, Fail.Incident> =
        context.tryGetBids()
            .orForwardFail { fail -> return fail }
            .details
            .flatMap { it.requirementResponses }
            .asSequence()
            .associate { requirementResponse ->
                val temporal = requirementResponse.id
                val permanent = RequirementResponseId.generate()
                temporal to permanent
            }
            .let { Ids.BidsRequirementResponses(it) }
            .asSuccess()

    private fun generatePermanentAuctionsIds(context: GlobalContext): Result<Ids, Fail.Incident> =
        context.tryGetTender()
            .bind { it.tryGetElectronicAuctions() }
            .orForwardFail { fail -> return fail }
            .details
            .associate { auction ->
                val temporal = auction.id
                val permanent = AuctionId.generate()
                temporal to permanent
            }
            .let { Ids.TenderAuctions(it) }
            .asSuccess()

    private fun updateAwardRequirementResponsesIds(
        context: CamundaGlobalContext,
        ids: Ids.AwardRequirementResponses
    ): MaybeFail<Fail.Incident.Bpms.Context> {
        val updatedAwards = context.getAwardsIfNotEmpty()
            .orReturnFail { return MaybeFail.fail(it) }
            .map { award ->
                val updatedRequirementResponses = award.getRequirementResponseIfNotEmpty()
                    .orReturnFail { return MaybeFail.fail(it) }
                    .map { requirementResponse ->
                        ids.getValue(requirementResponse.id)
                            .let { requirementResponse.copy(id = it) }
                    }
                    .let {
                        RequirementResponses(it)
                    }
                award.copy(requirementResponses = updatedRequirementResponses)
            }
            .let { Awards(it) }

        context.awards = updatedAwards

        return MaybeFail.none()
    }

    private fun updateSubmissionsBusinessFunctionIds(
        context: CamundaGlobalContext,
        ids: Ids.SubmissionBusinessFunctions
    ): MaybeFail<Fail.Incident.Bpms.Context> {
        fun BusinessFunction.update(ids: Ids.SubmissionBusinessFunctions) = copy(id = ids.getValue(id))

        fun Person.update(ids: Ids.SubmissionBusinessFunctions) = copy(
            businessFunctions = businessFunctions
                .map { person -> person.update(ids) }
                .let { BusinessFunctions(it) }
        )

        fun Organization.update(ids: Ids.SubmissionBusinessFunctions) = copy(
            persons = persons
                .map { person -> person.update(ids) }
                .let { Persons(it) }
        )

        fun Submission.update(ids: Ids.SubmissionBusinessFunctions) = copy(
            candidates = candidates
                .map { candidate -> candidate.update(ids) }
                .let { Candidates(it) }
        )

        val submissions = context.tryGetSubmissions()
            .orReturnFail { return MaybeFail.fail(it) }

        val updatedDetails = submissions.getDetailsIfNotEmpty()
            .orReturnFail { return MaybeFail.fail(it) }
            .map { submission -> submission.update(ids) }
            .let { Details(it) }

        val updatedSubmissions = submissions.copy(
            details = updatedDetails
        )

        context.submissions = updatedSubmissions

        return MaybeFail.none()
    }

    private fun updateBidBusinessFunctionIds(
        context: CamundaGlobalContext,
        ids: Ids.BidsBusinessFunctions
    ): MaybeFail<Fail.Incident.Bpms.Context> {
        fun BusinessFunction.update(ids: Ids.BidsBusinessFunctions) = copy(id = ids.getValue(id))

        fun Person.update(ids: Ids.BidsBusinessFunctions) = copy(
            businessFunctions = businessFunctions
                .map { person -> person.update(ids) }
                .let { BusinessFunctions(it) }
        )

        fun Organization.update(ids: Ids.BidsBusinessFunctions) = copy(
            persons = persons
                .map { person -> person.update(ids) }
                .let { Persons(it) }
        )

        fun Bid.update(ids: Ids.BidsBusinessFunctions) = copy(
            tenderers = tenderers
                .map { tenderer -> tenderer.update(ids) }
                .let { Organizations(it) }
        )

        val bids = context.tryGetBids()
            .orReturnFail { return MaybeFail.fail(it) }

        val updatedDetails = bids.getBidsDetailIfNotEmpty()
            .orReturnFail { return MaybeFail.fail(it) }
            .map { bid -> bid.update(ids) }
            .let { BidsDetails(it) }

        val updatedBids = bids.copy(
            details = updatedDetails
        )

        context.bids = updatedBids

        return MaybeFail.none()
    }

    private fun updateBidEvidenceIds(
        context: CamundaGlobalContext,
        ids: Ids.BidsEvidences
    ): MaybeFail<Fail.Incident.Bpms.Context> {

        fun Evidence.update(ids: Ids.BidsEvidences) = copy(id = ids.getValue(id))

        fun RequirementResponse.update(ids: Ids.BidsEvidences) = copy(
            evidences = evidences
                .map { evidence -> evidence.update(ids) }
                .let { Evidences(it) }
        )

        fun Bid.update(ids: Ids.BidsEvidences) = copy(
            requirementResponses = requirementResponses
                .map { requirementResponse -> requirementResponse.update(ids) }
                .let { RequirementResponses(it) }
        )

        val bids = context.tryGetBids()
            .orReturnFail { return MaybeFail.fail(it) }

        val updatedDetails = bids.getBidsDetailIfNotEmpty()
            .orReturnFail { return MaybeFail.fail(it) }
            .map { submission -> submission.update(ids) }
            .let { BidsDetails(it) }

        val updatedBids = bids.copy(
            details = updatedDetails
        )

        context.bids = updatedBids

        return MaybeFail.none()
    }

    private fun updateSubmissionEvidenceIds(
        context: CamundaGlobalContext,
        ids: Ids.SubmissionEvidence
    ): MaybeFail<Fail.Incident.Bpms.Context> {

        fun Evidence.update(ids: Ids.SubmissionEvidence) = copy(id = ids.getValue(id))

        fun RequirementResponse.update(ids: Ids.SubmissionEvidence) = copy(
            evidences = evidences
                .map { evidence -> evidence.update(ids) }
                .let { Evidences(it) }
        )

        fun Submission.update(ids: Ids.SubmissionEvidence) = copy(
            requirementResponses = requirementResponses
                .map { requirementResponse -> requirementResponse.update(ids) }
                .let { RequirementResponses(it) }
        )

        val submissions = context.tryGetSubmissions()
            .orReturnFail { return MaybeFail.fail(it) }

        val updatedDetails = submissions.getDetailsIfNotEmpty()
            .orReturnFail { return MaybeFail.fail(it) }
            .map { submission -> submission.update(ids) }
            .let { Details(it) }

        val updatedSubmissions = submissions.copy(
            details = updatedDetails
        )

        context.submissions = updatedSubmissions

        return MaybeFail.none()
    }

    private fun updateSubmissionsRequirementResponseIds(
        context: CamundaGlobalContext,
        ids: Ids.SubmissionsRequirementResponses
    ): MaybeFail<Fail.Incident.Bpms.Context> {
        val submissions = context.tryGetSubmissions()
            .orReturnFail { return MaybeFail.fail(it) }

        val updatedDetails = submissions.getDetailsIfNotEmpty()
            .orReturnFail { return MaybeFail.fail(it) }
            .map { submission ->
                val updatedRequirementResponse = submission.requirementResponses
                    .map { requirementResponse ->
                        ids.getValue(requirementResponse.id)
                            .let { requirementResponse.copy(id = it) }
                    }
                submission.copy(requirementResponses = RequirementResponses(updatedRequirementResponse))
            }
            .let { Details(it) }

        val updatedSubmissions = submissions.copy(details = updatedDetails)

        context.submissions = updatedSubmissions

        return MaybeFail.none()
    }

    private fun updateQualificationRequirementResponseIds(
        context: CamundaGlobalContext,
        ids: Ids.QualificationsRequirementResponses
    ): MaybeFail<Fail.Incident.Bpms.Context> {
        val qualifications = context.qualifications

        val updatedQualifications = qualifications
            .map { qualification ->
                val updatedRequirementResponse = qualification.requirementResponses
                    .map { requirementResponse ->
                        ids.getValue(requirementResponse.id)
                            .let { requirementResponse.copy(id = it) }
                    }
                qualification.copy(requirementResponses = RequirementResponses(updatedRequirementResponse))
            }
            .let { Qualifications(it) }

        context.qualifications = updatedQualifications

        return MaybeFail.none()
    }

    private fun updateBidRequirementResponseIds(
        context: CamundaGlobalContext,
        ids: Ids.BidsRequirementResponses
    ): MaybeFail<Fail.Incident.Bpms.Context> {

        val bids = context.tryGetBids()
            .orReturnFail { fail -> return MaybeFail.fail(fail) }

        val updatedBidsDetails = bids.details
            .map { bid ->
                val updatedRequirementResponse = bid.requirementResponses
                    .map { requirementResponse ->
                        val permanentId = ids.getValue(requirementResponse.id)
                        requirementResponse.copy(id = permanentId)
                    }
                bid.copy(requirementResponses = RequirementResponses(updatedRequirementResponse))
            }
            .let { BidsDetails(it) }

        context.bids = bids.copy(details = updatedBidsDetails)

        return MaybeFail.none()
    }

    private fun updateTenderAuctionsIds(
        context: CamundaGlobalContext,
        ids: Ids.TenderAuctions
    ): MaybeFail<Fail.Incident.Bpms.Context> {

        val tender = context.tryGetTender()
            .orReturnFail { fail -> return MaybeFail.fail(fail) }

        val updatedElectronicAuctions = tender.tryGetElectronicAuctions()
            .orReturnFail { fail -> return MaybeFail.fail(fail) }
            .details
            .map { auctionsDetail ->
                val permanentId = ids.getValue(auctionsDetail.id)
                auctionsDetail.copy(id = permanentId)
            }
            .let { ElectronicAuctions(details = ElectronicAuctionsDetails(it)) }

        context.tender = tender.copy(electronicAuctions = updatedElectronicAuctions)

        return MaybeFail.none()
    }

    private fun updateTenderAmendmentsIds(
        context: CamundaGlobalContext,
        ids: Ids.TenderAmendments
    ): MaybeFail<Fail.Incident.Bpms.Context> {
        val tender = context.tryGetTender()
            .orReturnFail { return MaybeFail.fail(it) }
        val amendments: List<Amendment> = tender.getAmendmentsIfNotEmpty()
            .orReturnFail { return MaybeFail.fail(it) }

        val updatedAmendments = Amendments(
            values = amendments.map { amendment ->
                ids.getValue(amendment.id)
                    .let { amendment.copy(id = it) }
            }
        )

        val updatedTender = tender.copy(
            amendments = updatedAmendments
        )

        context.tender = updatedTender

        return MaybeFail.none()
    }

    sealed class Ids : Serializable {
        class AwardRequirementResponses(values: Map<RequirementResponseId, RequirementResponseId> = emptyMap()) :
            Ids(), Map<RequirementResponseId, RequirementResponseId> by values, Serializable

        class BidsBusinessFunctions(values: Map<BusinessFunctionId, BusinessFunctionId> = emptyMap()) :
            Ids(), Map<BusinessFunctionId, BusinessFunctionId> by values, Serializable

        class BidsEvidences(values: Map<EvidenceId, EvidenceId> = emptyMap()) :
            Ids(), Map<EvidenceId, EvidenceId> by values, Serializable

        class BidsRequirementResponses(values: Map<RequirementResponseId, RequirementResponseId> = emptyMap()) :
            Ids(), Map<RequirementResponseId, RequirementResponseId> by values, Serializable

        class QualificationsRequirementResponses(values: Map<RequirementResponseId, RequirementResponseId> = emptyMap()) :
            Ids(), Map<RequirementResponseId, RequirementResponseId> by values, Serializable

        class SubmissionBusinessFunctions(values: Map<BusinessFunctionId, BusinessFunctionId> = emptyMap()) :
            Ids(), Map<BusinessFunctionId, BusinessFunctionId> by values, Serializable

        class SubmissionEvidence(values: Map<EvidenceId, EvidenceId> = emptyMap()) :
            Ids(), Map<EvidenceId, EvidenceId> by values, Serializable

        class SubmissionsRequirementResponses(values: Map<RequirementResponseId, RequirementResponseId> = emptyMap()) :
            Ids(), Map<RequirementResponseId, RequirementResponseId> by values, Serializable

        class TenderAmendments(values: Map<AmendmentId, AmendmentId> = emptyMap()) :
            Ids(), Map<AmendmentId, AmendmentId> by values, Serializable

        class TenderAuctions(values: Map<AuctionId, AuctionId> = emptyMap()) :
            Ids(), Map<AuctionId, AuctionId> by values, Serializable
    }

    class Parameters(val location: List<Location>)

    enum class Location(override val key: String) : EnumElementProvider.Key {

        AUCTION("AUCTION"),
        AWARD_REQUIREMENT_RESPONSE("AWARD.REQUIREMENTRESPONSE"),
        BID_BUSINESS_FUNCTION("BID.BUSINESSFUNCTION"),
        BID_EVIDENCE("BID.EVIDENCE"),
        BID_REQUIREMENT_RESPONSE("BID.REQUIREMENTRESPONSE"),
        QUALIFICATION_REQUIREMENT_RESPONSE("QUALIFICATION.REQUIREMENTRESPONSE"),
        SUBMISSION_BUSINESS_FUNCTION("SUBMISSION.BUSINESSFUNCTION"),
        SUBMISSION_EVIDENCE("SUBMISSION.EVIDENCE"),
        SUBMISSION_REQUIREMENT_RESPONSE("SUBMISSION.REQUIREMENTRESPONSE"),
        TENDER_AMENDMENT("TENDER.AMENDMENT");

        override fun toString(): String = key

        companion object : EnumElementProvider<Location>(info = info())
    }
}
