package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer

import com.procurement.orchestrator.application.model.context.CamundaContext
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.repository.ProcessInitializerRepository
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.model.contract.Contract
import com.procurement.orchestrator.domain.model.contract.ContractId
import com.procurement.orchestrator.domain.model.contract.Contracts
import com.procurement.orchestrator.domain.model.contract.confirmation.response.ConfirmationResponse
import com.procurement.orchestrator.domain.model.contract.confirmation.response.ConfirmationResponseId
import com.procurement.orchestrator.domain.model.contract.confirmation.response.ConfirmationResponses
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.identifier.Identifier
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunction
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctions
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.person.Person
import com.procurement.orchestrator.domain.model.person.PersonId
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.springframework.stereotype.Component

@Component
class BpeInitializeCreateConfirmationResponseForContractProcessDelegate(
    val logger: Logger,
    transform: Transform,
    operationStepRepository: OperationStepRepository,
    processInitializerRepository: ProcessInitializerRepository
) : AbstractInitializeProcessDelegate(logger, transform, operationStepRepository, processInitializerRepository) {

    override fun updateGlobalContext(
        camundaContext: CamundaContext,
        globalContext: CamundaGlobalContext
    ): MaybeFail<Fail> {

        val payload: CreateConfirmationResponseForContractProcess.Request.Payload =
            parsePayload(
                camundaContext.request.payload,
                CreateConfirmationResponseForContractProcess.Request.Payload::class.java
            )
                .orReturnFail { return MaybeFail.fail(it) }

        val contractId = ContractId.create(camundaContext.request.id!!)

        globalContext.contracts = Contracts(
            listOf(
                Contract(
                    id = contractId,
                    confirmationResponses = payload.confirmationResponse.let { confirmationResponse ->
                        ConfirmationResponse(
                            id = ConfirmationResponseId.generate(),
                            requestId = confirmationResponse.requestId,
                            type = confirmationResponse.type,
                            value = confirmationResponse.value,
                            relatedPerson = confirmationResponse.relatedPerson.let { relatedPerson ->
                                Person(
                                    id = PersonId.generate(
                                        scheme = relatedPerson.identifier.scheme,
                                        id = relatedPerson.identifier.id
                                    ),
                                    title = relatedPerson.title,
                                    name = relatedPerson.name,
                                    identifier = relatedPerson.identifier.let { identifier ->
                                        Identifier(
                                            scheme = identifier.scheme,
                                            id = identifier.id,
                                            uri = identifier.uri
                                        )
                                    },
                                    businessFunctions = relatedPerson.businessFunctions.map { businessFunction ->
                                        BusinessFunction(
                                            id = businessFunction.id,
                                            type = businessFunction.type,
                                            jobTitle = businessFunction.jobTitle,
                                            period = Period(startDate = businessFunction.period.startDate),
                                            documents = businessFunction.documents
                                                ?.map { document ->
                                                    Document(
                                                        id = document.id,
                                                        documentType = document.documentType,
                                                        title = document.title,
                                                        description = document.description
                                                    )
                                                }.orEmpty()
                                                .let { Documents(it) }

                                        )
                                    }.let { BusinessFunctions(it) }
                                )
                            }

                        )
                    }.let { ConfirmationResponses(listOf(it)) }

                )
            )
        )

        return MaybeFail.none()
    }
}
