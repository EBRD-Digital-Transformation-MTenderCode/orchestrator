package com.procurement.orchestrator.infrastructure.bpms.delegate.evaluation

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.EvaluationClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getAwardIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.award.Award
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.ValidateAwardDataAction
import org.springframework.stereotype.Component

@Component
class EvaluationValidateAwardDataDelegate(
    logger: Logger,
    private val evaluationClient: EvaluationClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, Unit>(
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
    ): Result<Reply<Unit>, Fail.Incident> {

        val processInfo = context.processInfo
        val cpid: Cpid = processInfo.cpid!!
        val ocid: Ocid = processInfo.ocid!!

        val tender = context.tryGetTender()
            .orForwardFail { return it }

        val award = context.getAwardIfOnlyOne()
            .orForwardFail { return it }

        return evaluationClient.validateAwardData(
            id = commandId,
            params = ValidateAwardDataAction.Params(
                cpid = cpid,
                ocid = ocid,
                operationType = processInfo.operationType,
                tender = tender.toRequest(),
                awards = listOf(award.toRequest())
            )
        )
    }

    private fun Tender.toRequest() = ValidateAwardDataAction.Params.Tender(
        lots = lots.first()
            .let { lot ->
                ValidateAwardDataAction.Params.Tender.Lot(
                    id = lot.id,
                    value = lot.value?.let { value ->
                        ValidateAwardDataAction.Params.Tender.Lot.Value(
                            amount = value.amount,
                            currency = value.currency
                        )
                    }
                )
            }.let { listOf(it) }
    )

    private fun Award.toRequest() = ValidateAwardDataAction.Params.Award(
        id = id,
        value = value
            ?.let { value ->
                ValidateAwardDataAction.Params.Award.Value(
                    amount = value.amount,
                    currency = value.currency
                )
            },
        documents = documents
            .map { document ->
                ValidateAwardDataAction.Params.Award.Document(
                    id = document.id,
                    description = document.description,
                    documentType = document.documentType,
                    title = document.title
                )
            },
        description = description,
        internalId = internalId,
        suppliers = suppliers.map { organization ->
            ValidateAwardDataAction.Params.Award.Supplier(
                id = organization.id,
                name = organization.name,
                address = organization.address
                    .let { address ->
                        ValidateAwardDataAction.Params.Award.Supplier.Address(
                            streetAddress = address?.streetAddress,
                            postalCode = address?.postalCode,
                            addressDetails = address?.addressDetails
                                ?.let { addressDetails ->
                                    ValidateAwardDataAction.Params.Award.Supplier.Address.AddressDetails(
                                        country = addressDetails.country
                                            .let { country ->
                                                ValidateAwardDataAction.Params.Award.Supplier.Address.AddressDetails.Country(
                                                    id = country.id,
                                                    description = country.description,
                                                    scheme = country.scheme
                                                )
                                            },
                                        region = addressDetails.region
                                            .let { region ->
                                                ValidateAwardDataAction.Params.Award.Supplier.Address.AddressDetails.Region(
                                                    id = region.id,
                                                    description = region.description,
                                                    scheme = region.scheme
                                                )
                                            },
                                        locality = addressDetails.locality
                                            .let { locality ->
                                                ValidateAwardDataAction.Params.Award.Supplier.Address.AddressDetails.Locality(
                                                    id = locality.id,
                                                    description = locality.description,
                                                    scheme = locality.scheme
                                                )
                                            }
                                    )
                                }
                        )
                    },
                identifier = organization.identifier
                    ?.let { identifier ->
                        ValidateAwardDataAction.Params.Award.Supplier.Identifier(
                            id = identifier.id,
                            legalName = identifier.legalName,
                            scheme = identifier.scheme,
                            uri = identifier.uri
                        )
                    },
                additionalIdentifiers = organization.additionalIdentifiers
                    .map { additionalIdentifier ->
                        ValidateAwardDataAction.Params.Award.Supplier.AdditionalIdentifier(
                            id = additionalIdentifier.id,
                            legalName = additionalIdentifier.legalName,
                            scheme = additionalIdentifier.scheme,
                            uri = additionalIdentifier.uri
                        )
                    },
                contactPoint = organization.contactPoint
                    ?.let { contactPoint ->
                        ValidateAwardDataAction.Params.Award.Supplier.ContactPoint(
                            name = contactPoint.name,
                            telephone = contactPoint.telephone,
                            faxNumber = contactPoint.faxNumber,
                            email = contactPoint.email,
                            url = contactPoint.url
                        )
                    },
                persones = organization.persons
                    .map { person ->
                        ValidateAwardDataAction.Params.Award.Supplier.Persone(
                            id = person.id,
                            title = person.title.toString(),
                            name = person.name,
                            identifier = person.identifier
                                ?.let { identifier ->
                                    ValidateAwardDataAction.Params.Award.Supplier.Persone.Identifier(
                                        id = identifier.id,
                                        scheme = identifier.scheme,
                                        uri = identifier.uri
                                    )
                                },
                            businessFunctions = person.businessFunctions
                                .map { businessFunction ->
                                    ValidateAwardDataAction.Params.Award.Supplier.Persone.BusinessFunction(
                                        id = businessFunction.id,
                                        type = businessFunction.type,
                                        jobTitle = businessFunction.jobTitle,
                                        period = businessFunction.period
                                            ?.let { period ->
                                                ValidateAwardDataAction.Params.Award.Supplier.Persone.BusinessFunction.Period(
                                                    startDate = period.startDate
                                                )
                                            },
                                        documents = businessFunction.documents
                                            .map { document ->
                                                ValidateAwardDataAction.Params.Award.Supplier.Persone.BusinessFunction.Document(
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
                        ValidateAwardDataAction.Params.Award.Supplier.Details(
                            typeOfSupplier = details.typeOfSupplier,
                            mainEconomicActivities = details.mainEconomicActivities
                                .map { mainEconomicActivity ->
                                    ValidateAwardDataAction.Params.Award.Supplier.Details.MainEconomicActivity(
                                        scheme = mainEconomicActivity.scheme,
                                        id = mainEconomicActivity.id,
                                        description = mainEconomicActivity.description,
                                        uri = mainEconomicActivity.uri
                                    )
                                },
                            bankAccounts = details.bankAccounts
                                .map { bankAccount ->
                                    ValidateAwardDataAction.Params.Award.Supplier.Details.BankAccount(
                                        description = bankAccount.description,
                                        bankName = bankAccount.bankName,
                                        address = bankAccount.address
                                            ?.let { address ->
                                                ValidateAwardDataAction.Params.Award.Supplier.Details.BankAccount.Address(
                                                    streetAddress = address.streetAddress,
                                                    postalCode = address.postalCode,
                                                    addressDetails = address.addressDetails
                                                        ?.let { addressDetails ->
                                                            ValidateAwardDataAction.Params.Award.Supplier.Details.BankAccount.Address.AddressDetails(
                                                                country = addressDetails.country
                                                                    .let { country ->
                                                                        ValidateAwardDataAction.Params.Award.Supplier.Details.BankAccount.Address.AddressDetails.Country(
                                                                            scheme = country.scheme,
                                                                            id = country.id,
                                                                            description = country.description
                                                                        )
                                                                    },
                                                                region = addressDetails.region
                                                                    .let { region ->
                                                                        ValidateAwardDataAction.Params.Award.Supplier.Details.BankAccount.Address.AddressDetails.Region(
                                                                            scheme = region.scheme,
                                                                            id = region.id,
                                                                            description = region.description
                                                                        )
                                                                    },
                                                                locality = addressDetails.locality
                                                                    .let { locality ->
                                                                        ValidateAwardDataAction.Params.Award.Supplier.Details.BankAccount.Address.AddressDetails.Locality(
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
                                            ?.let { accountIdentifier ->
                                                ValidateAwardDataAction.Params.Award.Supplier.Details.BankAccount.Identifier(
                                                    scheme = accountIdentifier.scheme,
                                                    id = accountIdentifier.id
                                                )
                                            },
                                        accountIdentification = bankAccount.accountIdentification
                                            ?.let { accountIdentifier ->
                                                ValidateAwardDataAction.Params.Award.Supplier.Details.BankAccount.AccountIdentification(
                                                    scheme = accountIdentifier.scheme,
                                                    id = accountIdentifier.id
                                                )

                                            },
                                        additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers
                                            .map { accountIdentifier ->
                                                ValidateAwardDataAction.Params.Award.Supplier.Details.BankAccount.AdditionalAccountIdentifier(
                                                    scheme = accountIdentifier.scheme,
                                                    id = accountIdentifier.id
                                                )
                                            }
                                    )
                                },
                            legalForm = details.legalForm
                                ?.let { legalForm ->
                                    ValidateAwardDataAction.Params.Award.Supplier.Details.LegalForm(
                                        scheme = legalForm.scheme,
                                        id = legalForm.id,
                                        description = legalForm.description,
                                        uri = legalForm.uri
                                    )
                                },
                            scale = details.scale,
                            permits = details.permits
                                .map { permit ->
                                    ValidateAwardDataAction.Params.Award.Supplier.Details.Permit(
                                        scheme = permit.scheme,
                                        id = permit.id,
                                        uri = permit.url,
                                        permitDetails = permit.permitDetails
                                            ?.let { permitDetails ->
                                                ValidateAwardDataAction.Params.Award.Supplier.Details.Permit.PermitDetails(
                                                    issuedBy = permitDetails.issuedBy
                                                        ?.let { issuedBy ->
                                                            ValidateAwardDataAction.Params.Award.Supplier.Details.Permit.PermitDetails.IssuedBy(
                                                                id = issuedBy.id,
                                                                name = issuedBy.name
                                                            )
                                                        },
                                                    issuedThought = permitDetails.issuedThought
                                                        ?.let { issuedThought ->
                                                            ValidateAwardDataAction.Params.Award.Supplier.Details.Permit.PermitDetails.IssuedThought(
                                                                id = issuedThought.id,
                                                                name = issuedThought.name
                                                            )
                                                        },
                                                    validityPeriod = permitDetails.validityPeriod
                                                        ?.let { validityPeriod ->
                                                            ValidateAwardDataAction.Params.Award.Supplier.Details.Permit.PermitDetails.ValidityPeriod(
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
        result: Option<Unit>
    ): MaybeFail<Fail.Incident.Bpmn> = MaybeFail.none()
}
