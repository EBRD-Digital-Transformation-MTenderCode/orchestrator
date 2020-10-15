package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer

import com.procurement.orchestrator.application.model.context.CamundaContext
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.repository.ProcessInitializerRepository
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.model.classification.Classification
import com.procurement.orchestrator.domain.model.contract.observation.Observation
import com.procurement.orchestrator.domain.model.contract.observation.Observations
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.item.Item
import com.procurement.orchestrator.domain.model.item.ItemId
import com.procurement.orchestrator.domain.model.item.Items
import com.procurement.orchestrator.domain.model.item.unit.ItemUnit
import com.procurement.orchestrator.domain.model.lot.Lot
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.lot.Lots
import com.procurement.orchestrator.domain.model.lot.RelatedLots
import com.procurement.orchestrator.domain.model.lot.Variant
import com.procurement.orchestrator.domain.model.lot.Variants
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.tender.ProcurementMethodModalities
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.domain.model.tender.auction.ElectronicAuctionModalities
import com.procurement.orchestrator.domain.model.tender.auction.ElectronicAuctionModality
import com.procurement.orchestrator.domain.model.tender.auction.ElectronicAuctions
import com.procurement.orchestrator.domain.model.tender.auction.ElectronicAuctionsDetail
import com.procurement.orchestrator.domain.model.tender.auction.ElectronicAuctionsDetails
import com.procurement.orchestrator.domain.model.tender.conversion.Conversion
import com.procurement.orchestrator.domain.model.tender.conversion.Conversions
import com.procurement.orchestrator.domain.model.tender.conversion.coefficient.Coefficient
import com.procurement.orchestrator.domain.model.tender.conversion.coefficient.Coefficients
import com.procurement.orchestrator.domain.model.tender.criteria.Criteria
import com.procurement.orchestrator.domain.model.tender.criteria.Criterion
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroup
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroups
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.Requirements
import com.procurement.orchestrator.domain.model.tender.target.Target
import com.procurement.orchestrator.domain.model.tender.target.Targets
import com.procurement.orchestrator.domain.model.value.Value
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.springframework.stereotype.Component

@Component
class BpeInitializeCreatePreAwardCatalogRequestProcessDelegate(
    val logger: Logger,
    transform: Transform,
    operationStepRepository: OperationStepRepository,
    processInitializerRepository: ProcessInitializerRepository
) : AbstractInitializeProcessDelegate(logger, transform, operationStepRepository, processInitializerRepository) {

    override fun updateGlobalContext(
        camundaContext: CamundaContext,
        globalContext: CamundaGlobalContext
    ): MaybeFail<Fail> {

        val payload: CreatePreAwardCatalogRequest.Request.Payload =
            parsePayload(camundaContext.request.payload, CreatePreAwardCatalogRequest.Request.Payload::class.java)
                .orReturnFail { return MaybeFail.fail(it) }

        globalContext.tender = initializeTender(payload.tender)

        return MaybeFail.none()
    }

    private fun initializeTender(tender: CreatePreAwardCatalogRequest.Request.Payload.Tender): Tender =
        Tender(
            title = tender.title,
            description = tender.description,
            awardCriteria = tender.awardCriteria,
            awardCriteriaDetails = tender.awardCriteriaDetails,
            criteria = tender.criteria
                ?.map { criterion ->
                    Criterion(
                        id = criterion.id,
                        title = criterion.title,
                        description = criterion.description,
                        relatesTo = criterion.relatesTo,
                        source = null,
                        relatedItem = criterion.relatedItem,
                        requirementGroups = criterion.requirementGroups
                            .map { requirementGroup ->
                                RequirementGroup(
                                    id = requirementGroup.id,
                                    description = requirementGroup.description,
                                    requirements = Requirements(requirementGroup.requirements)
                                )
                            }
                            .let { RequirementGroups(it) }
                    )
                }
                .orEmpty()
                .let { Criteria(it) },

            conversions = tender.conversions
                ?.map { conversion ->
                    Conversion(
                        id = conversion.id,
                        description = conversion.description,
                        relatedItem = conversion.relatedItem,
                        relatesTo = conversion.relatesTo,
                        rationale = conversion.rationale,
                        coefficients = conversion.coefficients
                            .map { coefficient ->
                                Coefficient(
                                    id = coefficient.id,
                                    value = coefficient.value,
                                    coefficient = coefficient.coefficient
                                )
                            }
                            .let { Coefficients(it) }
                    )
                }
                .orEmpty()
                .let { Conversions(it) },

            lots = tender.lots
                .map { lot ->
                    Lot(
                        id = LotId.create(lot.id),
                        internalId = lot.internalId,
                        title = lot.title,
                        description = lot.description,
                        variants = lot.variants
                            .map { variant ->
                                Variant(hasVariants = variant.hasVariants)
                            }
                            .let { Variants(it) }
                    )
                }
                .let { Lots(it) },

            items = tender.items
                ?.map { item ->
                    Item(
                        id = ItemId.create(item.id),
                        description = item.description,
                        classification = item.classification
                            .let { classification ->
                                Classification(
                                    id = classification.id,
                                    scheme = classification.scheme
                                )
                            },
                        unit = ItemUnit(id = item.unit.id),
                        relatedLot = LotId.create(item.relatedLot)
                    )
                }
                .orEmpty()
                .let { Items(it) },

            tenderPeriod = tender.tenderPeriod
                ?.let { period ->
                    Period(
                        endDate = period.endDate
                    )
                },
            electronicAuctions = tender.electronicAuctions
                ?.let { electronicAuctions ->
                    ElectronicAuctions(
                        details = electronicAuctions.details
                            .map { detail ->
                                ElectronicAuctionsDetail(
                                    id = detail.id,
                                    relatedLot = LotId.create(detail.relatedLot),
                                    electronicAuctionModalities = detail.electronicAuctionModalities
                                        .map { modality ->
                                            ElectronicAuctionModality(
                                                eligibleMinimumDifference = Value(
                                                    currency = modality.eligibleMinimumDifference.currency
                                                )
                                            )
                                        }
                                        .let { ElectronicAuctionModalities(it) }
                                )
                            }
                            .let { ElectronicAuctionsDetails(it) }
                    )
                },
            procurementMethodModalities = tender.procurementMethodModalities
                .orEmpty()
                .let { ProcurementMethodModalities(it) },

            documents = tender.documents
                ?.map { document ->
                    Document(
                        id = document.id,
                        title = document.title,
                        documentType = document.documentType,
                        description = document.description,
                        relatedLots = document.relatedLots
                            ?.map { LotId.create(it) }
                            .orEmpty()
                            .let { RelatedLots(it) }
                    )
                }
                .orEmpty()
                .let { Documents(it) },
            targets = tender.targets
                ?.map { target ->
                    Target(
                        id = target.id,
                        relatesTo = target.relatesTo,
                        relatedItem = target.relatedItem,
                        title = target.title,
                        observations = target.observations
                            .map { observation ->
                                Observation(
                                    id = observation.id,
                                    unit = ItemUnit(id = observation.unit.id),
                                    notes = observation.notes,
                                    measure = observation.measure
                                )
                            }
                            .let { Observations(it) }
                    )
                }
                .orEmpty()
                .let { Targets(it) },

            classification = tender.classification
                .let { classification ->
                    Classification(
                        id = classification.id,
                        scheme = classification.scheme,
                        description = null,
                        uri = null
                    )
                }
        )
}
