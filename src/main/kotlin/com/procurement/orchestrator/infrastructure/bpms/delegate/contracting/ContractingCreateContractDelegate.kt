package com.procurement.orchestrator.infrastructure.bpms.delegate.contracting

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.ContractingClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.model.context.members.Outcomes
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.ProcurementMethodDetails
import com.procurement.orchestrator.domain.model.contract.RelatedProcess
import com.procurement.orchestrator.domain.model.contract.RelatedProcessTypes
import com.procurement.orchestrator.domain.model.contract.RelatedProcesses
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.contracting.ContractingCommands
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CreateContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.converToTenderObject
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.convertToAwardObject
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.convertToContractObject
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class ContractingCreateContractDelegate(
    logger: Logger,
    private val contractingClient: ContractingClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, CreateContractAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {
    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        Unit.asSuccess()

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<CreateContractAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo

        val tender = context.tryGetTender()
            .orForwardFail { incident -> return incident }

        val relatedProcess = context.processInfo.relatedProcess!!
        val relatedOcid: Ocid = relatedProcess.ocid
            ?: return Result.failure(
                Fail.Incident.Bpms.Context.Missing(
                    name = "relatedProcess.ocid",
                    path = "processInfo"
                )
            )

        return contractingClient.createContract(
            id = commandId,
            params = CreateContractAction.Params(
                cpid = processInfo.cpid!!,
                date = requestInfo.timestamp,
                pmd = processInfo.pmd,
                relatedOcid = relatedOcid,
                owner = requestInfo.owner,
                tender = tender.let { tender ->
                    CreateContractAction.Params.Tender(
                        classification = tender.classification!!.let { classification ->
                            CreateContractAction.Params.Tender.Classification(
                                id = classification.id,
                                scheme = classification.scheme,
                                description = classification.description!!
                            )
                        },
                        procurementMethod = tender.procurementMethod!!,
                        procurementMethodDetails = tender.procurementMethodDetails!!,
                        mainProcurementCategory = tender.mainProcurementCategory!!,
                        lots = tender.lots.map { lot ->
                            CreateContractAction.Params.Tender.Lot(
                                id = lot.id,
                                internalId = lot.internalId,
                                title = lot.title!!,
                                description = lot.description,
                                placeOfPerformance = lot.placeOfPerformance!!.let { placeOfPerformance ->
                                    CreateContractAction.Params.Tender.Lot.PlaceOfPerformance(
                                        description = placeOfPerformance.description,
                                        address = placeOfPerformance.address!!.let { address ->
                                            CreateContractAction.Params.Tender.Lot.PlaceOfPerformance.Address(
                                                streetAddress = address.streetAddress!!,
                                                postalCode = address.postalCode,
                                                addressDetails = address.addressDetails!!.let { addressDetails ->
                                                    CreateContractAction.Params.Tender.Lot.PlaceOfPerformance.Address.AddressDetails(
                                                        country = addressDetails.country.let { country ->
                                                            CreateContractAction.Params.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Country(
                                                                id = country.id,
                                                                description = country.description!!,
                                                                scheme = country.scheme,
                                                                uri = country.uri!!
                                                            )
                                                        },
                                                        region = addressDetails.region.let { region ->
                                                            CreateContractAction.Params.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Region(
                                                                id = region.id,
                                                                scheme = region.scheme,
                                                                description = region.description!!,
                                                                uri = region.uri!!
                                                            )
                                                        },
                                                        locality = addressDetails.locality.let { locality ->
                                                            CreateContractAction.Params.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Locality(
                                                                id = locality.id,
                                                                scheme = locality.scheme,
                                                                description = locality.description!!,
                                                                uri = locality.uri
                                                            )
                                                        }
                                                    )
                                                }
                                            )
                                        }
                                    )
                                }
                            )
                        },
                        items = tender.items.map { item ->
                            CreateContractAction.Params.Tender.Item(
                                id = item.id,
                                internalId = item.internalId,
                                classification = item.classification!!.let { classification ->
                                    CreateContractAction.Params.Tender.Item.Classification(
                                        id = classification.id,
                                        scheme = classification.scheme,
                                        description = classification.description!!
                                    )
                                },
                                additionalClassifications = item.additionalClassifications.map { additionalClassification ->
                                    CreateContractAction.Params.Tender.Item.AdditionalClassification(
                                        id = additionalClassification.id,
                                        scheme = additionalClassification.scheme,
                                        description = additionalClassification.description!!
                                    )
                                },
                                unit = item.unit!!.let { unit ->
                                    CreateContractAction.Params.Tender.Item.Unit(
                                        id = unit.id,
                                        name = unit.name!!
                                    )
                                },
                                description = item.description!!,
                                relatedLot = item.relatedLot!!,
                                quantity = item.quantity!!
                            )
                        },
                        additionalProcurementCategories = tender.additionalProcurementCategories
                    )
                },
                awards = context.awards.map { award ->
                    CreateContractAction.Params.Award(
                        id = award.id,
                        value = award.value!!.let { value ->
                            CreateContractAction.Params.Award.Value(
                                amount = value.amount!!,
                                currency = value.currency!!
                            )
                        },
                        relatedLots = award.relatedLots,
                        suppliers = award.suppliers.map { supplier ->
                            CreateContractAction.Params.Award.Supplier(
                                id = supplier.id,
                                name = supplier.name!!
                            )
                        },
                        documents = award.documents.map { document ->
                            CreateContractAction.Params.Award.Document(
                                id = document.id,
                                documentType = document.documentType!!,
                                description = document.description,
                                title = document.title!!,
                                relatedLots = document.relatedLots.map { it }
                            )
                        }
                    )
                },
                parties = context.parties.map { party ->
                    CreateContractAction.Params.Party(
                        id = party.id,
                        name = party.name!!,
                        identifier = party.identifier!!.let { identifier ->
                            CreateContractAction.Params.Party.Identifier(
                                id = identifier.id,
                                legalName = identifier.legalName!!,
                                scheme = identifier.scheme,
                                uri = identifier.uri
                            )
                        },
                        additionalIdentifiers = party.additionalIdentifiers.map { additionalIdentifier ->
                            CreateContractAction.Params.Party.AdditionalIdentifier(
                                id = additionalIdentifier.id,
                                legalName = additionalIdentifier.legalName!!,
                                scheme = additionalIdentifier.scheme,
                                uri = additionalIdentifier.uri
                            )
                        },
                        address = party.address!!.let { address ->
                            CreateContractAction.Params.Party.Address(
                                streetAddress = address.streetAddress!!,
                                postalCode = address.postalCode,
                                addressDetails = address.addressDetails!!.let { addressDetails ->
                                    CreateContractAction.Params.Party.Address.AddressDetails(
                                        country = addressDetails.country.let { country ->
                                            CreateContractAction.Params.Party.Address.AddressDetails.Country(
                                                id = country.id,
                                                description = country.description!!,
                                                scheme = country.scheme,
                                                uri = country.uri!!
                                            )
                                        },
                                        region = addressDetails.region.let { region ->
                                            CreateContractAction.Params.Party.Address.AddressDetails.Region(
                                                id = region.id,
                                                description = region.description!!,
                                                scheme = region.scheme,
                                                uri = region.uri!!
                                            )
                                        },
                                        locality = addressDetails.locality.let { locality ->
                                            CreateContractAction.Params.Party.Address.AddressDetails.Locality(
                                                id = locality.id,
                                                description = locality.description!!,
                                                scheme = locality.scheme,
                                                uri = locality.uri
                                            )
                                        }
                                    )
                                }
                            )
                        },
                        persones = party.persons.map { person ->
                            CreateContractAction.Params.Party.Persone(
                                title = person.title!!,
                                id = person.id,
                                name = person.name!!,
                                identifier = person.identifier!!.let { identifier ->
                                    CreateContractAction.Params.Party.Persone.Identifier(
                                        id = identifier.id,
                                        scheme = identifier.scheme,
                                        uri = identifier.uri
                                    )
                                },
                                businessFunctions = person.businessFunctions.map { businessFunction ->
                                    CreateContractAction.Params.Party.Persone.BusinessFunction(
                                        id = businessFunction.id,
                                        type = businessFunction.type!!,
                                        jobTitle = businessFunction.jobTitle!!,
                                        period = businessFunction.period!!.let { period ->
                                            CreateContractAction.Params.Party.Persone.BusinessFunction.Period(
                                                startDate = period.startDate!!
                                            )
                                        },
                                        documents = businessFunction.documents.map { document ->
                                            CreateContractAction.Params.Party.Persone.BusinessFunction.Document(
                                                id = document.id,
                                                documentType = document.documentType!!,
                                                title = document.title!!,
                                                description = document.description
                                            )
                                        }
                                    )
                                }
                            )
                        },
                        details = party.details!!.let { details ->
                            CreateContractAction.Params.Party.Details(
                                typeOfSupplier = details.typeOfSupplier,
                                mainEconomicActivities = details.mainEconomicActivities.map { mainEconomicActivity ->
                                    CreateContractAction.Params.Party.Details.MainEconomicActivity(
                                        id = mainEconomicActivity.id,
                                        scheme = mainEconomicActivity.scheme,
                                        description = mainEconomicActivity.description!!,
                                        uri = mainEconomicActivity.uri
                                    )
                                },
                                scale = details.scale!!,
                                permits = details.permits.map { permit ->
                                    CreateContractAction.Params.Party.Details.Permit(
                                        id = permit.id,
                                        scheme = permit.scheme,
                                        url = permit.url,
                                        permitDetails = permit.permitDetails!!.let { permitDetails ->
                                            CreateContractAction.Params.Party.Details.Permit.PermitDetails(
                                                issuedBy = permitDetails.issuedBy!!.let { issuedBy ->
                                                    CreateContractAction.Params.Party.Details.Permit.PermitDetails.IssuedBy(
                                                        id = issuedBy.id,
                                                        name = issuedBy.name!!
                                                    )
                                                },
                                                issuedThought = permitDetails.issuedThought!!.let { issuedThought ->
                                                    CreateContractAction.Params.Party.Details.Permit.PermitDetails.IssuedThought(
                                                        id = issuedThought.id,
                                                        name = issuedThought.name!!
                                                    )
                                                },
                                                validityPeriod = permitDetails.validityPeriod!!.let { period ->
                                                    CreateContractAction.Params.Party.Details.Permit.PermitDetails.ValidityPeriod(
                                                        startDate = period.startDate!!,
                                                        endDate = period.endDate
                                                    )
                                                }
                                            )
                                        }
                                    )
                                },
                                bankAccounts = details.bankAccounts.map { bankAccount ->
                                    CreateContractAction.Params.Party.Details.BankAccount(
                                        description = bankAccount.description!!,
                                        bankName = bankAccount.bankName!!,
                                        address = bankAccount.address!!.let { address ->
                                            CreateContractAction.Params.Party.Details.BankAccount.Address(
                                                streetAddress = address.streetAddress!!,
                                                postalCode = address.postalCode,
                                                addressDetails = address.addressDetails!!.let { addressDetails ->
                                                    CreateContractAction.Params.Party.Details.BankAccount.Address.AddressDetails(
                                                        country = addressDetails.country.let { country ->
                                                            CreateContractAction.Params.Party.Details.BankAccount.Address.AddressDetails.Country(
                                                                id = country.id,
                                                                scheme = country.scheme,
                                                                description = country.description!!
                                                            )
                                                        },
                                                        region = addressDetails.region.let { region ->
                                                            CreateContractAction.Params.Party.Details.BankAccount.Address.AddressDetails.Region(
                                                                id = region.id,
                                                                description = region.description!!,
                                                                scheme = region.scheme
                                                            )
                                                        },
                                                        locality = addressDetails.locality.let { locality ->
                                                            CreateContractAction.Params.Party.Details.BankAccount.Address.AddressDetails.Locality(
                                                                id = locality.id,
                                                                description = locality.description!!,
                                                                scheme = locality.scheme
                                                            )
                                                        }
                                                    )
                                                }
                                            )
                                        },
                                        identifier = bankAccount.identifier!!.let { identifier ->
                                            CreateContractAction.Params.Party.Details.BankAccount.Identifier(
                                                id = identifier.id,
                                                scheme = identifier.scheme
                                            )
                                        },
                                        accountIdentification = bankAccount.accountIdentification!!.let { accountIdentification ->
                                            CreateContractAction.Params.Party.Details.BankAccount.AccountIdentification(
                                                id = accountIdentification.id,
                                                scheme = accountIdentification.scheme
                                            )
                                        },
                                        additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers.map { accountIdentifier ->
                                            CreateContractAction.Params.Party.Details.BankAccount.AdditionalAccountIdentifier(
                                                scheme = accountIdentifier.scheme,
                                                id = accountIdentifier.id
                                            )
                                        }
                                    )
                                },
                                legalForm = details.legalForm?.let { legalForm ->
                                    CreateContractAction.Params.Party.Details.LegalForm(
                                        scheme = legalForm.scheme,
                                        id = legalForm.scheme,
                                        description = legalForm.description!!,
                                        uri = legalForm.uri
                                    )
                                }
                            )
                        },
                        roles = party.roles.map { it.name },
                        contactPoint = party.contactPoint!!.let { contactPoint ->
                            CreateContractAction.Params.Party.ContactPoint(
                                name = contactPoint.name!!,
                                email = contactPoint.email!!,
                                telephone = contactPoint.telephone!!,
                                faxNumber = contactPoint.faxNumber,
                                url = contactPoint.url
                            )
                        }
                    )
                }
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<CreateContractAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    ExternalServiceName.CONTRACTING,
                    ContractingCommands.CreateContract
                )
            )

        context.processInfo = context.processInfo.copy(ocid = data.ocid)

        val receivedRelatedProcesses = data.relatedProcesses.map { it.convert() }
        context.relatedProcesses = RelatedProcesses(receivedRelatedProcesses)

        context.tender = data.converToTenderObject()

        context.awards = data.convertToAwardObject()

        context.contracts = data.convertToContractObject()

        context.outcomes = createOutcomes(context, data)

        return MaybeFail.none()
    }

    private fun CreateContractAction.Result.RelatedProcesse.convert() =
        RelatedProcess(
            id = id,
            identifier = identifier,
            relationship = RelatedProcessTypes(relationship),
            scheme = scheme,
            uri = uri
        )

    private fun createOutcomes(
        context: CamundaGlobalContext,
        result: CreateContractAction.Result
    ): Outcomes {
        val platformId = context.requestInfo.platformId
        val outcomes = context.outcomes ?: Outcomes()
        val details = outcomes[platformId] ?: Outcomes.Details()

        val updatedDetails =
            when (context.processInfo.pmd) {
                ProcurementMethodDetails.DCO, ProcurementMethodDetails.TEST_DCO,
                ProcurementMethodDetails.MC, ProcurementMethodDetails.TEST_MC,
                ProcurementMethodDetails.RFQ, ProcurementMethodDetails.TEST_RFQ ->
                    details.copy(po = result.contracts
                        .map { contract ->
                            Outcomes.Details.PO(id = contract.id, token = result.token)
                        }
                    )
                else -> details.copy(ac = result.contracts
                    .map { contract ->
                        Outcomes.Details.AC(id = contract.id, token = result.token)
                    }
                )
            }

        outcomes[platformId] = updatedDetails
        return outcomes
    }
}