package com.procurement.orchestrator.exceptions

enum class ErrorType constructor(val code: String, val message: String) {
    DATA_NOT_FOUND("00.01", "Data not found."),
    IMPLEMENTATION_ERROR("00.02", "No implementation for this type of operation."),
    PARAM_ERROR("00.03", "Should not be empty for this type of operation"),
    MS_NOT_FOUND("00.04", "MS not found."),
    RECORD_NOT_FOUND("00.05", "Record not found."),
    AWARD_NOT_FOUND("00.06", "Award not found."),
    BID_NOT_FOUND("00.07", "Bid not found."),
    LOT_NOT_FOUND("00.08", "Lot not found."),
    ENQUIRY_NOT_FOUND("00.09", "Enquiry not found."),
    STAGE_ERROR("00.10", "Stage invalid for this type of operation."),
    BREAKDOWN_ERROR("00.11", "Budget breakdown is empty."),
    OCID_ERROR("00.12", "OCID is empty."),
    HISTORY_ERROR("00.12", "OCID is empty."),
    CONTRACT_BY_ID_NOT_FOUND("00.13", "The contract by id for cancel is not found."),
    CONTEXT("00.15", "Context parameter not found."),
    INVALID_STAGE("00.16", "Invalid stage"),
    BIDS_IN_REQUEST_IS_EMPTY("00.17", "The collection of bids in request cannot be empty"),
    CONTRACT_NOT_FOUND("00.18", "Contract is not found."),
    INVALID_PMD("00.19", "Invalid pmd."),
    INVALID_REQUIREMENT_VALUE("00.20", "Invalid requirement value."),
    IS_EMPTY("00.21", "List of items is empty.");
}
