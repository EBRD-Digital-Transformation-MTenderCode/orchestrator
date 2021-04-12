package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer

import com.procurement.orchestrator.application.model.context.CamundaContext
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.repository.ProcessInitializerRepository
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.model.address.Address
import com.procurement.orchestrator.domain.model.address.AddressDetails
import com.procurement.orchestrator.domain.model.address.country.CountryDetails
import com.procurement.orchestrator.domain.model.address.locality.LocalityDetails
import com.procurement.orchestrator.domain.model.address.region.RegionDetails
import com.procurement.orchestrator.domain.model.classification.Classification
import com.procurement.orchestrator.domain.model.item.Item
import com.procurement.orchestrator.domain.model.item.ItemId
import com.procurement.orchestrator.domain.model.item.Items
import com.procurement.orchestrator.domain.model.lot.Lot
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.lot.Lots
import com.procurement.orchestrator.domain.model.lot.PlaceOfPerformance
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.tender.ProcurementMethodModalities
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.domain.model.tender.auction.AuctionId
import com.procurement.orchestrator.domain.model.tender.auction.ElectronicAuctionModalities
import com.procurement.orchestrator.domain.model.tender.auction.ElectronicAuctionModality
import com.procurement.orchestrator.domain.model.tender.auction.ElectronicAuctions
import com.procurement.orchestrator.domain.model.tender.auction.ElectronicAuctionsDetail
import com.procurement.orchestrator.domain.model.tender.auction.ElectronicAuctionsDetails
import com.procurement.orchestrator.domain.model.unit.Unit
import com.procurement.orchestrator.domain.model.value.Value
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.springframework.stereotype.Component

@Component
class BpeInitializeCreateRfqProcessDelegate(
    val logger: Logger,
    transform: Transform,
    operationStepRepository: OperationStepRepository,
    processInitializerRepository: ProcessInitializerRepository
) : AbstractInitializeProcessDelegate(logger, transform, operationStepRepository, processInitializerRepository) {

    override fun updateGlobalContext(
        camundaContext: CamundaContext,
        globalContext: CamundaGlobalContext
    ): MaybeFail<Fail> {

        val payload: CreateRfqRequest.Request.Payload =
            parsePayload(camundaContext.request.payload, CreateRfqRequest.Request.Payload::class.java)
                .orReturnFail { return MaybeFail.fail(it) }

        globalContext.tender = initializeTender(payload.tender)

        return MaybeFail.none()
    }

    private fun initializeTender(tender: CreateRfqRequest.Request.Payload.Tender): Tender =
        Tender(
            lots = initializeLots(tender.lots),
            items = initializeItems(tender.items),
            tenderPeriod = initializeTenderPeriod(tender.tenderPeriod),
            electronicAuctions = initializeElectronicAuctions(tender.electronicAuctions),
            procurementMethodModalities = tender.procurementMethodModalities
                .orEmpty()
                .let { ProcurementMethodModalities(it) }
        )

    private fun initializeLots(lots: List<CreateRfqRequest.Request.Payload.Tender.Lot>): Lots =
        lots
            .map { lot ->
                Lot(
                    id = LotId.create(lot.id),
                    internalId = lot.internalId,
                    title = lot.title,
                    description = lot.description,
                    value = Value(currency = lot.value.currency),
                    contractPeriod = lot.contractPeriod.let { period ->
                        Period(
                            startDate = period.startDate,
                            endDate = period.endDate
                        )
                    },
                    placeOfPerformance = lot.placeOfPerformance.let { placeOfPerformance ->
                        PlaceOfPerformance(
                            description = placeOfPerformance.description,
                            address = placeOfPerformance.address.let { address ->
                                Address(
                                    streetAddress = address.streetAddress,
                                    postalCode = address.postalCode,
                                    addressDetails = address.addressDetails.let { addressDetails ->
                                        AddressDetails(
                                            country = addressDetails.country.let { country ->
                                                CountryDetails(
                                                    id = country.id,
                                                    scheme = country.scheme,
                                                    description = country.description
                                                )
                                            },
                                            region = addressDetails.region.let { region ->
                                                RegionDetails(
                                                    id = region.id,
                                                    scheme = region.scheme,
                                                    description = region.description
                                                )
                                            },
                                            locality = addressDetails.locality.let { locality ->
                                                LocalityDetails(
                                                    id = locality.id,
                                                    scheme = locality.scheme,
                                                    description = locality.description
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
            .let { Lots(it) }

    private fun initializeItems(items: List<CreateRfqRequest.Request.Payload.Tender.Item>): Items =
        items
            .map { item ->
                Item(
                    id = ItemId.create(item.id),
                    description = item.description,
                    quantity = item.quantity,
                    internalId = item.internalId,
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

    private fun initializeTenderPeriod(tenderPeriod: CreateRfqRequest.Request.Payload.Tender.TenderPeriod): Period =
        tenderPeriod.let { period -> Period(endDate = period.endDate) }

    private fun initializeElectronicAuctions(electronicAuctions: CreateRfqRequest.Request.Payload.Tender.ElectronicAuctions?): ElectronicAuctions? =
        electronicAuctions
            ?.let { _electronicAuctions ->
                ElectronicAuctions(
                    details = _electronicAuctions.details
                        .map { detail ->
                            ElectronicAuctionsDetail(
                                id = AuctionId.create(detail.id),
                                relatedLot = LotId.create(detail.relatedLot),
                                electronicAuctionModalities = detail.electronicAuctionModalities
                                    .map { modality ->
                                        ElectronicAuctionModality(
                                            eligibleMinimumDifference = Value(
                                                amount = modality.eligibleMinimumDifference.amount,
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
}
