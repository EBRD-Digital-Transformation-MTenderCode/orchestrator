package com.procurement.orchestrator.infrastructure.bpms.delegate.evaluation

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.EvaluationClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getAwardIfOnlyOne
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.address.Address
import com.procurement.orchestrator.domain.model.address.AddressDetails
import com.procurement.orchestrator.domain.model.address.country.CountryDetails
import com.procurement.orchestrator.domain.model.address.locality.LocalityDetails
import com.procurement.orchestrator.domain.model.address.region.RegionDetails
import com.procurement.orchestrator.domain.model.award.Award
import com.procurement.orchestrator.domain.model.award.Awards
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.identifier.Identifier
import com.procurement.orchestrator.domain.model.identifier.Identifiers
import com.procurement.orchestrator.domain.model.lot.RelatedLots
import com.procurement.orchestrator.domain.model.organization.ContactPoint
import com.procurement.orchestrator.domain.model.organization.Organization
import com.procurement.orchestrator.domain.model.organization.Organizations
import com.procurement.orchestrator.domain.model.organization.datail.Details
import com.procurement.orchestrator.domain.model.organization.datail.MainEconomicActivities
import com.procurement.orchestrator.domain.model.organization.datail.MainEconomicActivity
import com.procurement.orchestrator.domain.model.organization.datail.account.AccountIdentifier
import com.procurement.orchestrator.domain.model.organization.datail.account.AccountIdentifiers
import com.procurement.orchestrator.domain.model.organization.datail.account.BankAccount
import com.procurement.orchestrator.domain.model.organization.datail.account.BankAccounts
import com.procurement.orchestrator.domain.model.organization.datail.legalform.LegalForm
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunction
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctions
import com.procurement.orchestrator.domain.model.party.Parties
import com.procurement.orchestrator.domain.model.party.Party
import com.procurement.orchestrator.domain.model.party.PartyRole
import com.procurement.orchestrator.domain.model.party.PartyRoles
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.person.Person
import com.procurement.orchestrator.domain.model.person.Persons
import com.procurement.orchestrator.domain.model.value.Value
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.evaluation.EvaluationCommands
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.UpdateAwardAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class EvaluationUpdateAwardDelegate(
    logger: Logger,
    private val evaluationClient: EvaluationClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, UpdateAwardAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<UpdateAwardAction.Result>, Fail.Incident> {

        val award = context.getAwardIfOnlyOne()
            .orForwardFail { return it }

        val processInfo = context.processInfo
        return evaluationClient.updateAward(
            id = commandId,
            params = UpdateAwardAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                awards = listOf(award.toRequest())
            )
        )
    }

    private fun Award.toRequest() = UpdateAwardAction.Params.Award(
        id = id,
        value = value
            ?.let { value ->
                UpdateAwardAction.Params.Award.Value(
                    amount = value.amount
                )
            },
        documents = documents
            .map { document ->
                UpdateAwardAction.Params.Award.Document(
                    id = document.id,
                    description = document.description,
                    documentType = document.documentType,
                    title = document.title
                )
            },
        description = description,
        internalId = internalId,
        suppliers = suppliers.map { organization ->
            UpdateAwardAction.Params.Award.Supplier(
                id = organization.id,
                name = organization.name,
                address = organization.address
                    ?.let { address ->
                        UpdateAwardAction.Params.Award.Supplier.Address(
                            streetAddress = address.streetAddress,
                            postalCode = address.postalCode,
                            addressDetails = address.addressDetails
                                ?.let { addressDetails ->
                                    UpdateAwardAction.Params.Award.Supplier.Address.AddressDetails(
                                        country = addressDetails.country
                                            .let { country ->
                                                UpdateAwardAction.Params.Award.Supplier.Address.AddressDetails.Country(
                                                    id = country.id,
                                                    description = country.description,
                                                    scheme = country.scheme,
                                                    uri = country.uri
                                                )
                                            },
                                        region = addressDetails.region
                                            .let { region ->
                                                UpdateAwardAction.Params.Award.Supplier.Address.AddressDetails.Region(
                                                    id = region.id,
                                                    description = region.description,
                                                    scheme = region.scheme,
                                                    uri = region.uri
                                                )
                                            },
                                        locality = addressDetails.locality
                                            .let { locality ->
                                                UpdateAwardAction.Params.Award.Supplier.Address.AddressDetails.Locality(
                                                    id = locality.id,
                                                    description = locality.description,
                                                    scheme = locality.scheme,
                                                    uri = locality.uri
                                                )
                                            }
                                    )
                                }
                        )
                    },
                identifier = organization.identifier
                    ?.let { identifier ->
                        UpdateAwardAction.Params.Award.Supplier.Identifier(
                            id = identifier.id,
                            legalName = identifier.legalName,
                            scheme = identifier.scheme,
                            uri = identifier.uri
                        )
                    },
                additionalIdentifiers = organization.additionalIdentifiers
                    .map { additionalIdentifier ->
                        UpdateAwardAction.Params.Award.Supplier.AdditionalIdentifier(
                            id = additionalIdentifier.id,
                            legalName = additionalIdentifier.legalName,
                            scheme = additionalIdentifier.scheme,
                            uri = additionalIdentifier.uri
                        )
                    },
                contactPoint = organization.contactPoint
                    ?.let { contactPoint ->
                        UpdateAwardAction.Params.Award.Supplier.ContactPoint(
                            name = contactPoint.name,
                            telephone = contactPoint.telephone,
                            faxNumber = contactPoint.faxNumber,
                            email = contactPoint.email,
                            url = contactPoint.url
                        )
                    },
                persones = organization.persons
                    .map { person ->
                        UpdateAwardAction.Params.Award.Supplier.Persone(
                            id = person.id,
                            title = person.title,
                            name = person.name,
                            identifier = person.identifier
                                ?.let { identifier ->
                                    UpdateAwardAction.Params.Award.Supplier.Persone.Identifier(
                                        id = identifier.id,
                                        scheme = identifier.scheme,
                                        uri = identifier.uri
                                    )
                                },
                            businessFunctions = person.businessFunctions
                                .map { businessFunction ->
                                    UpdateAwardAction.Params.Award.Supplier.Persone.BusinessFunction(
                                        id = businessFunction.id,
                                        type = businessFunction.type,
                                        jobTitle = businessFunction.jobTitle,
                                        period = businessFunction.period
                                            ?.let { period ->
                                                UpdateAwardAction.Params.Award.Supplier.Persone.BusinessFunction.Period(
                                                    startDate = period.startDate
                                                )
                                            },
                                        documents = businessFunction.documents
                                            .map { document ->
                                                UpdateAwardAction.Params.Award.Supplier.Persone.BusinessFunction.Document(
                                                    id = document.id,
                                                    title = document.title,
                                                    description = document.description,
                                                    documentType = document.documentType
                                                )
                                            }

                                    )
                                }
                        )
                    },
                details = organization.details
                    ?.let { details ->
                        UpdateAwardAction.Params.Award.Supplier.Details(
                            typeOfSupplier = details.typeOfSupplier,
                            mainEconomicActivities = details.mainEconomicActivities
                                .map { mainEconomicActivity ->
                                    UpdateAwardAction.Params.Award.Supplier.Details.MainEconomicActivity(
                                        scheme = mainEconomicActivity.scheme,
                                        id = mainEconomicActivity.id,
                                        description = mainEconomicActivity.description,
                                        uri = mainEconomicActivity.uri
                                    )
                                },
                            bankAccounts = details.bankAccounts
                                .map { bankAccount ->
                                    UpdateAwardAction.Params.Award.Supplier.Details.BankAccount(
                                        description = bankAccount.description,
                                        bankName = bankAccount.bankName,
                                        address = bankAccount.address
                                            ?.let { address ->
                                                UpdateAwardAction.Params.Award.Supplier.Details.BankAccount.Address(
                                                    streetAddress = address.streetAddress,
                                                    postalCode = address.postalCode,
                                                    addressDetails = address.addressDetails
                                                        ?.let { addressDetails ->
                                                            UpdateAwardAction.Params.Award.Supplier.Details.BankAccount.Address.AddressDetails(
                                                                country = addressDetails.country
                                                                    .let { country ->
                                                                        UpdateAwardAction.Params.Award.Supplier.Details.BankAccount.Address.AddressDetails.Country(
                                                                            scheme = country.scheme,
                                                                            id = country.id,
                                                                            description = country.description,
                                                                            uri = country.uri
                                                                        )
                                                                    },
                                                                region = addressDetails.region
                                                                    .let { region ->
                                                                        UpdateAwardAction.Params.Award.Supplier.Details.BankAccount.Address.AddressDetails.Region(
                                                                            scheme = region.scheme,
                                                                            id = region.id,
                                                                            description = region.description,
                                                                            uri = region.uri
                                                                        )
                                                                    },
                                                                locality = addressDetails.locality
                                                                    .let { locality ->
                                                                        UpdateAwardAction.Params.Award.Supplier.Details.BankAccount.Address.AddressDetails.Locality(
                                                                            scheme = locality.scheme,
                                                                            id = locality.id,
                                                                            description = locality.description,
                                                                            uri = locality.uri
                                                                        )
                                                                    }
                                                            )
                                                        }
                                                )
                                            },
                                        identifier = bankAccount.identifier
                                            ?.let { accountIdentifier ->
                                                UpdateAwardAction.Params.Award.Supplier.Details.BankAccount.Identifier(
                                                    scheme = accountIdentifier.scheme,
                                                    id = accountIdentifier.id
                                                )
                                            },
                                        accountIdentification = bankAccount.accountIdentification
                                            ?.let { accountIdentifier ->
                                                UpdateAwardAction.Params.Award.Supplier.Details.BankAccount.AccountIdentification(
                                                    scheme = accountIdentifier.scheme,
                                                    id = accountIdentifier.id
                                                )

                                            },
                                        additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers
                                            .map { accountIdentifier ->
                                                UpdateAwardAction.Params.Award.Supplier.Details.BankAccount.AdditionalAccountIdentifier(
                                                    scheme = accountIdentifier.scheme,
                                                    id = accountIdentifier.id
                                                )
                                            }
                                    )
                                },
                            legalForm = details.legalForm
                                ?.let { legalForm ->
                                    UpdateAwardAction.Params.Award.Supplier.Details.LegalForm(
                                        scheme = legalForm.scheme,
                                        id = legalForm.id,
                                        description = legalForm.description,
                                        uri = legalForm.uri
                                    )
                                },
                            scale = details.scale,
                            permits = details.permits
                                .map { permit ->
                                    UpdateAwardAction.Params.Award.Supplier.Details.Permit(
                                        scheme = permit.scheme,
                                        id = permit.id,
                                        url = permit.url,
                                        permitDetails = permit.permitDetails
                                            ?.let { permitDetails ->
                                                UpdateAwardAction.Params.Award.Supplier.Details.Permit.PermitDetails(
                                                    issuedBy = permitDetails.issuedBy
                                                        ?.let { issuedBy ->
                                                            UpdateAwardAction.Params.Award.Supplier.Details.Permit.PermitDetails.IssuedBy(
                                                                id = issuedBy.id,
                                                                name = issuedBy.name
                                                            )
                                                        },
                                                    issuedThought = permitDetails.issuedThought
                                                        ?.let { issuedThought ->
                                                            UpdateAwardAction.Params.Award.Supplier.Details.Permit.PermitDetails.IssuedThought(
                                                                id = issuedThought.id,
                                                                name = issuedThought.name
                                                            )
                                                        },
                                                    validityPeriod = permitDetails.validityPeriod
                                                        ?.let { validityPeriod ->
                                                            UpdateAwardAction.Params.Award.Supplier.Details.Permit.PermitDetails.ValidityPeriod(
                                                                startDate = validityPeriod.startDate,
                                                                endDate = validityPeriod.startDate
                                                            )
                                                        }
                                                )
                                            }
                                    )
                                }
                        )
                    }
            )
        }
    )

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<UpdateAwardAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.EVALUATION,
                    action = EvaluationCommands.UpdateAward
                )
            )

        val firstAward = data.awards.first()
        val receivedParties = createParties(firstAward)

        val receivedSuppliers = createSuppliers(firstAward)
        val receivedAward = createAward(firstAward)
        val updatedAward = context.awards.first()
            .updateBy(receivedAward)
            .copy(suppliers = receivedSuppliers)

        context.awards = Awards(listOf(updatedAward))
        context.parties = receivedParties

        return MaybeFail.none()
    }

    private fun createAward(receivedAward: UpdateAwardAction.Result.Award) = Award(
        id = receivedAward.id,
        internalId = receivedAward.internalId,
        description = receivedAward.description,
        value = receivedAward.value
            .let { value ->
                Value(
                    amount = value.amount,
                    currency = value.currency
                )
            },
        date = receivedAward.date,
        status = receivedAward.status,
        statusDetails = receivedAward.statusDetails,
        relatedLots = receivedAward.relatedLots.let { RelatedLots(it) },
        documents = receivedAward.documents
            ?.map { document ->
                Document(
                    id = document.id,
                    title = document.title,
                    documentType = document.documentType,
                    description = document.description
                )
            }
            .orEmpty()
            .let { Documents(it) }
    )

    private fun createParties(receivedAward: UpdateAwardAction.Result.Award) =
        receivedAward.suppliers.asSequence()
            .map { party -> party.toDomain() }
            .map { party -> party.copy(roles = PartyRoles(listOf(PartyRole.SUPPLIER))) }
            .toList()
            .let { Parties(it) }

    private fun createSuppliers(
        receivedAward: UpdateAwardAction.Result.Award
    ): Organizations = receivedAward.suppliers
        .map { supplier ->
            Organization(
                name = supplier.name,
                id = supplier.id
            )
        }
        .let { Organizations(it) }

    private fun UpdateAwardAction.Result.Award.Supplier.toDomain(): Party =
        Party(
            id = id,
            name = name,
            additionalIdentifiers = additionalIdentifiers
                ?.map { additionalIdentifier ->
                    Identifier(
                        id = additionalIdentifier.id,
                        scheme = additionalIdentifier.scheme,
                        legalName = additionalIdentifier.legalName,
                        uri = additionalIdentifier.uri
                    )

                }.orEmpty()
                .let { Identifiers(it) },
            address = address
                .let { address ->
                    Address(
                        streetAddress = address.streetAddress,
                        postalCode = address.postalCode,
                        addressDetails = address.addressDetails
                            .let { addressDetails ->
                                AddressDetails(
                                    country = addressDetails.country
                                        .let { country ->
                                            CountryDetails(
                                                id = country.id,
                                                description = country.description,
                                                scheme = country.scheme,
                                                uri = country.uri
                                            )
                                        },
                                    region = addressDetails.region
                                        .let { region ->
                                            RegionDetails(
                                                id = region.id,
                                                description = region.description,
                                                scheme = region.scheme,
                                                uri = region.uri
                                            )
                                        },
                                    locality = addressDetails.locality
                                        .let { locality ->
                                            LocalityDetails(
                                                id = locality.id,
                                                description = locality.description,
                                                scheme = locality.scheme,
                                                uri = locality.uri
                                            )
                                        }
                                )
                            }
                    )
                },
            contactPoint = contactPoint
                .let { contactPoint ->
                    ContactPoint(
                        name = contactPoint.name,
                        email = contactPoint.email,
                        telephone = contactPoint.telephone,
                        faxNumber = contactPoint.faxNumber,
                        url = contactPoint.url
                    )
                },
            persons = persones
                ?.map { person ->
                    Person(
                        id = person.id,
                        identifier = person.identifier
                            .let { identifier ->
                                Identifier(
                                    scheme = identifier.scheme,
                                    id = identifier.id,
                                    uri = identifier.uri
                                )
                            },
                        name = person.name,
                        title = person.title.key,
                        businessFunctions = person.businessFunctions
                            .map { businessFunction ->
                                BusinessFunction(
                                    id = businessFunction.id,
                                    type = businessFunction.type,
                                    jobTitle = businessFunction.jobTitle,
                                    period = Period(startDate = businessFunction.period.startDate),
                                    documents = businessFunction.documents
                                        ?.map { document ->
                                            Document(
                                                documentType = document.documentType,
                                                id = document.id,
                                                title = document.title,
                                                description = document.description
                                            )
                                        }
                                        .orEmpty()
                                        .let { Documents(it) }
                                )
                            }
                            .let { BusinessFunctions(it) }
                    )
                }
                .orEmpty()
                .let { Persons(it) },
            details = details
                .let { details ->
                    Details(
                        typeOfSupplier = details.typeOfSupplier,
                        mainEconomicActivities = details.mainEconomicActivities
                            ?.map { mainEconomicActivity ->
                                MainEconomicActivity(
                                    scheme = mainEconomicActivity.scheme,
                                    id = mainEconomicActivity.id,
                                    description = mainEconomicActivity.description,
                                    uri = mainEconomicActivity.uri
                                )
                            }
                            .orEmpty()
                            .let { MainEconomicActivities(it) },
                        bankAccounts = details.bankAccounts
                            ?.map { bankAccount ->
                                BankAccount(
                                    description = bankAccount.description,
                                    bankName = bankAccount.bankName,
                                    address = bankAccount.address
                                        .let { address ->
                                            Address(
                                                streetAddress = address.streetAddress,
                                                postalCode = address.postalCode,
                                                addressDetails = address.addressDetails
                                                    .let { addressDetails ->
                                                        AddressDetails(
                                                            country = addressDetails.country
                                                                .let { country ->
                                                                    CountryDetails(
                                                                        scheme = country.scheme,
                                                                        id = country.id,
                                                                        description = country.description
                                                                    )
                                                                },
                                                            region = addressDetails.region
                                                                .let { region ->
                                                                    RegionDetails(
                                                                        scheme = region.scheme,
                                                                        id = region.id,
                                                                        description = region.description
                                                                    )
                                                                },
                                                            locality = addressDetails.locality
                                                                .let { locality ->
                                                                    LocalityDetails(
                                                                        scheme = locality.scheme,
                                                                        id = locality.id,
                                                                        description = locality.description
                                                                    )
                                                                }
                                                        )
                                                    }
                                            )
                                        },
                                    identifier = bankAccount.identifier
                                        .let { accountIdentifier ->
                                            AccountIdentifier(
                                                scheme = accountIdentifier.scheme,
                                                id = accountIdentifier.id
                                            )
                                        },
                                    accountIdentification = bankAccount.accountIdentification
                                        .let { accountIdentifier ->
                                            AccountIdentifier(
                                                scheme = accountIdentifier.scheme,
                                                id = accountIdentifier.id
                                            )

                                        },
                                    additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers
                                        ?.map { accountIdentifier ->
                                            AccountIdentifier(
                                                scheme = accountIdentifier.scheme,
                                                id = accountIdentifier.id
                                            )
                                        }
                                        .orEmpty()
                                        .let { AccountIdentifiers(it) }
                                )
                            }
                            .orEmpty()
                            .let { BankAccounts(it) },
                        legalForm = details.legalForm
                            ?.let { legalForm ->
                                LegalForm(
                                    scheme = legalForm.scheme,
                                    id = legalForm.id,
                                    description = legalForm.description,
                                    uri = legalForm.uri
                                )
                            },
                        scale = details.scale
                    )
                }

        )
}


