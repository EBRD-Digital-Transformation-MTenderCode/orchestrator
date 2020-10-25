package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer

import com.procurement.orchestrator.application.model.context.CamundaContext
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.repository.ProcessInitializerRepository
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.model.classification.Classification
import com.procurement.orchestrator.domain.model.contract.observation.Dimensions
import com.procurement.orchestrator.domain.model.contract.observation.Observation
import com.procurement.orchestrator.domain.model.contract.observation.Observations
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.item.Item
import com.procurement.orchestrator.domain.model.item.ItemId
import com.procurement.orchestrator.domain.model.item.Items
import com.procurement.orchestrator.domain.model.lot.Lot
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.lot.Lots
import com.procurement.orchestrator.domain.model.lot.RelatedLots
import com.procurement.orchestrator.domain.model.lot.Variant
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
import com.procurement.orchestrator.domain.model.unit.Unit
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
            criteria = initializeCriteria(tender.criteria.orEmpty()),
            conversions = initializeConversions(tender.conversions.orEmpty()),
            lots = initializeLots(tender.lots),
            items = initializeItems(tender.items.orEmpty()),
            documents = initializeDocuments(tender.documents.orEmpty()),
            targets = initializeTargets(tender.targets.orEmpty()),
            tenderPeriod = initializeTenderPeriod(tender.tenderPeriod),
            electronicAuctions = initializeElectronicAuctions(tender.electronicAuctions),
            classification = initializeClassification(tender.classification),
            procurementMethodModalities = tender.procurementMethodModalities
                .orEmpty()
                .let { ProcurementMethodModalities(it) }
        )

    private fun initializeCriteria(criteria: List<CreatePreAwardCatalogRequest.Request.Payload.Tender.Criteria>): Criteria =
        criteria
            .map { criterion ->
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
            .let { Criteria(it) }

    private fun initializeConversions(conversions: List<CreatePreAwardCatalogRequest.Request.Payload.Tender.Conversion>): Conversions =
        conversions
            .map { conversion ->
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
            .let { Conversions(it) }

    private fun initializeLots(lots: List<CreatePreAwardCatalogRequest.Request.Payload.Tender.Lot>): Lots =
        lots
            .map { lot ->
                Lot(
                    id = LotId.create(lot.id),
                    internalId = lot.internalId,
                    title = lot.title,
                    description = lot.description,
                    variants = lot.variants
                        .let { variant ->
                            Variant(
                                hasVariants = variant.hasVariants,
                                variantDetails = variant.variantsDetails
                            )
                        },
                    classification = lot.classification
                        .let { classification ->
                            Classification(id = classification.id, scheme = classification.scheme)
                        }
                )
            }
            .let { Lots(it) }

    private fun initializeItems(items: List<CreatePreAwardCatalogRequest.Request.Payload.Tender.Item>): Items =
        items
            .map { item ->
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
                    unit = Unit(id = item.unit.id),
                    relatedLot = LotId.create(item.relatedLot)
                )
            }
            .let { Items(it) }

    private fun initializeDocuments(documents: List<CreatePreAwardCatalogRequest.Request.Payload.Tender.Document>): Documents =
        documents
            .map { document ->
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
            .let { Documents(it) }

    private fun initializeTargets(targets: List<CreatePreAwardCatalogRequest.Request.Payload.Tender.Target>): Targets =
        targets
            .map { target ->
                Target(
                    id = target.id,
                    relatesTo = target.relatesTo,
                    relatedItem = target.relatedItem,
                    title = target.title,
                    observations = target.observations
                        .map { observation ->
                            Observation(
                                id = observation.id,
                                unit = Unit(id = observation.unit.id),
                                notes = observation.notes,
                                measure = observation.measure,
                                period = observation.period
                                    ?.let { period ->
                                        Period(
                                            startDate = period.startDate,
                                            endDate = period.endDate
                                        )
                                    },
                                dimensions = observation.dimensions
                                    ?.let { dimensions ->
                                        Dimensions(requirementClassIdPR = dimensions.requirementClassIdPR)
                                    },
                                relatedRequirementId = observation.relatedRequirementId
                            )
                        }
                        .let { Observations(it) }
                )
            }
            .let { Targets(it) }

    private fun initializeTenderPeriod(tenderPeriod: CreatePreAwardCatalogRequest.Request.Payload.Tender.TenderPeriod?): Period? =
        tenderPeriod
            ?.let { period ->
                Period(
                    endDate = period.endDate
                )
            }

    private fun initializeElectronicAuctions(electronicAuctions: CreatePreAwardCatalogRequest.Request.Payload.Tender.ElectronicAuctions?): ElectronicAuctions? =
        electronicAuctions
            ?.let { _electronicAuctions ->
                ElectronicAuctions(
                    details = _electronicAuctions.details
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
            }

    private fun initializeClassification(classification: CreatePreAwardCatalogRequest.Request.Payload.Tender.Classification): Classification =
        Classification(
            id = classification.id,
            scheme = classification.scheme,
            description = null,
            uri = null
        )
}
