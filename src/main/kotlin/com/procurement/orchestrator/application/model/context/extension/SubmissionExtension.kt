package com.procurement.orchestrator.application.model.context.extension

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.organization.Organization
import com.procurement.orchestrator.domain.model.submission.Submission
import com.procurement.orchestrator.domain.model.submission.Submissions

private const val NAME_DETAILS = "details"
private const val NAME_CANDIDATES = "candidates"
private const val NAME_DOCUMENTS = "documents"

private const val PATH_SUBMISSIONS = "submissions"

fun Submissions.getDetailsIfNotEmpty(): Result<List<Submission>, Fail.Incident.Bpms.Context> =
    this.details.getIfNotEmpty(name = NAME_DETAILS, path = PATH_SUBMISSIONS)

fun Submission.getCandidatesIfNotEmpty(): Result<List<Organization>, Fail.Incident.Bpms.Context> =
    this.candidates.getIfNotEmpty(name = NAME_CANDIDATES, path = PATH_SUBMISSIONS)

fun Submission.getDocumentsIfNotEmpty(): Result<List<Document>, Fail.Incident.Bpms.Context> =
    this.documents.getIfNotEmpty(name = NAME_DOCUMENTS, path = PATH_SUBMISSIONS)

fun Submissions.getDetailsIfOnlyOne(): Result<Submission, Fail.Incident.Bpms.Context> =
    this.details.getElementIfOnlyOne(name = NAME_DETAILS, path = PATH_SUBMISSIONS)

