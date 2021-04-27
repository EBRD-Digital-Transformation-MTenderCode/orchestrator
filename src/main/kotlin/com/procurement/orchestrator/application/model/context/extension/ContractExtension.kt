package com.procurement.orchestrator.application.model.context.extension

import com.procurement.orchestrator.application.model.context.GlobalContext
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.contract.Contract
import com.procurement.orchestrator.domain.model.document.Document

private const val NAME = "contracts"

fun Contract.getDocumentsIfNotEmpty(): Result<List<Document>, Fail.Incident.Bpms.Context> =
    this.documents.getIfNotEmpty(name = "$NAME.documents")

fun GlobalContext.getContractIfOnlyOne(): Result<Contract, Fail.Incident.Bpms.Context> =
    this.contracts.getElementIfOnlyOne(name = NAME)

