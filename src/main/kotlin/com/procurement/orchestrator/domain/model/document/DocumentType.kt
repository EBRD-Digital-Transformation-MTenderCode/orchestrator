package com.procurement.orchestrator.domain.model.document

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class DocumentType(@JsonValue override val key: String) : EnumElementProvider.Key {

    ASSET_AND_LIABILITY_ASSESSMENT("assetAndLiabilityAssessment"),
    AWARD_NOTICE("awardNotice"),
    BIDDERS("bidders"),
    BIDDING_DOCUMENTS("biddingDocuments"),
    BILL_OF_QUANTITY("billOfQuantity"),
    BUYERS_RESPONSE_ADD("buyersResponseAdd"),
    CANCELLATION_DETAILS("cancellationDetails"),
    CLARIFICATIONS("clarifications"),
    COMPLAINTS("complaints"),
    COMPLETION_CERTIFICATE("completionCertificate"),
    CONFLICT_OF_INTEREST("conflictOfInterest"),
    CONTRACT_ANNEXE("contractAnnexe"),
    CONTRACT_ARRANGEMENTS("contractArrangements"),
    CONTRACT_DRAFT("contractDraft"),
    CONTRACT_GUARANTEES("contractGuarantees"),
    CONTRACT_NOTICE("contractNotice"),
    CONTRACT_SCHEDULE("contractSchedule"),
    CONTRACT_SIGNED("contractSigned"),
    CONTRACT_SUMMARY("contractSummary"),
    ELIGIBILITY_CRITERIA("eligibilityCriteria"),
    ELIGIBILITY_DOCUMENTS("x_eligibilityDocuments"),
    ENVIRONMENTAL_IMPACT("environmentalImpact"),
    EVALUATION_CRITERIA("evaluationCriteria"),
    EVALUATION_REPORTS("evaluationReports"),
    FEASIBILITY_STUDY("feasibilityStudy"),
    HEARING_NOTICE("hearingNotice"),
    ILLUSTRATION("illustration"),
    MARKET_STUDIES("marketStudies"),
    NEEDS_ASSESSMENT("needsAssessment"),
    NOTICE("notice"),
    PROCUREMENT_PLAN("procurementPlan"),
    PROJECT_PLAN("projectPlan"),
    REGULATORY_DOCUMENT("regulatoryDocument"),
    RISK_PROVISIONS("riskProvisions"),
    SHORTLISTED_FIRMS("shortlistedFirms"),
    SUB_CONTRACT("subContract"),
    SUBMISSION_DOCUMENTS("submissionDocuments"),
    TECHNICAL_SPECIFICATIONS("technicalSpecifications"),
    TENDER_NOTICE("tenderNotice"),
    WINNING_BID("winningBid"),
    X_COMMERCIAL_OFFER("x_commercialOffer"),
    X_ELIGIBILITY_DOCUMENTS("x_eligibilityDocuments"),
    X_FRAMEWORK_PROJECT("x_frameworkProject"),
    X_QUALIFICATION_DOCUMENTS("x_qualificationDocuments"),
    X_TECHNICAL_DOCUMENTS("x_technicalDocuments"),
    ;

    override fun toString(): String = key

    companion object : EnumElementProvider<DocumentType>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
