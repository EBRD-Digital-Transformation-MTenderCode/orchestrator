package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer

import com.procurement.orchestrator.application.model.context.CamundaContext
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.repository.ProcessInitializerRepository
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.extension.date.tryParseLocalDateTime
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.fail.error.DataValidationErrors
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.address.Address
import com.procurement.orchestrator.domain.model.address.AddressDetails
import com.procurement.orchestrator.domain.model.address.country.CountryDetails
import com.procurement.orchestrator.domain.model.address.locality.LocalityDetails
import com.procurement.orchestrator.domain.model.address.region.RegionDetails
import com.procurement.orchestrator.domain.model.item.Item
import com.procurement.orchestrator.domain.model.item.ItemId
import com.procurement.orchestrator.domain.model.item.Items
import com.procurement.orchestrator.domain.model.lot.Lot
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.lot.Lots
import com.procurement.orchestrator.domain.model.lot.Option
import com.procurement.orchestrator.domain.model.lot.Options
import com.procurement.orchestrator.domain.model.lot.PlaceOfPerformance
import com.procurement.orchestrator.domain.model.lot.RecurrentProcurement
import com.procurement.orchestrator.domain.model.lot.Renewal
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.period.Periods
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.domain.model.value.Value
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.springframework.stereotype.Component

@Component
class BpeInitializeDivideLotProcessDelegate(
    val logger: Logger,
    transform: Transform,
    operationStepRepository: OperationStepRepository,
    processInitializerRepository: ProcessInitializerRepository
) : AbstractInitializeProcessDelegate(logger, transform, operationStepRepository, processInitializerRepository) {

    override fun updateGlobalContext(
        camundaContext: CamundaContext,
        globalContext: CamundaGlobalContext
    ): MaybeFail<Fail> {

        val payload: DivideLotProcess.Request.Payload =
            parsePayload(camundaContext.request.payload, DivideLotProcess.Request.Payload::class.java)
                .orReturnFail { return MaybeFail.fail(it) }

        globalContext.tender = initializeTender(payload)
            .orReturnFail { return MaybeFail.fail(it) }

        return MaybeFail.none()
    }

    private fun initializeTender(payload: DivideLotProcess.Request.Payload): Result<Tender, Fail> =
        payload.tender.let { tender ->
            Tender(
                lots = tender.lots
                    .map { lot -> lot.toDomain().orForwardFail { return it } }
                    .let { Lots((it)) },
                items = tender.items
                    .map { item -> item.toDomain() }
                    .let { Items(it) }
            ).asSuccess()
        }

    private fun DivideLotProcess.Request.Payload.Tender.Lot.toDomain(): Result<Lot, Fail> =
        Lot(
            id = LotId.create(this.id),
            internalId = this.internalId,
            title = this.title,
            description = this.description,
            value = this.value.toDomain(),
            contractPeriod = this.contractPeriod.toDomain().orForwardFail { return it },
            placeOfPerformance = this.placeOfPerformance.toDomain(),
            hasOptions = this.hasOptions,
            options = this.options
                ?.map { option -> option.toDomain().orForwardFail { return it } }
                .orEmpty()
                .let { Options(it) },
            hasRenewal = this.hasRenewal,
            renewal = this.renewal?.toDomain()?.orForwardFail { return it },
            hasRecurrence = this.hasRecurrence,
            recurrence = this.recurrence?.toDomain()?.orForwardFail { return it }
        )
            .asSuccess()

    private fun DivideLotProcess.Request.Payload.Tender.Lot.Renewal.toDomain():  Result<Renewal, Fail> {
        return Renewal(
            description = description,
            minimumRenewals = minimumRenewals,
            maximumRenewals = maximumRenewals,
            period = period?.toDomain()?.orForwardFail { return it }
        ).asSuccess()
    }

    private fun DivideLotProcess.Request.Payload.Tender.Lot.Renewal.Period.toDomain(): Result<Period, Fail> {
        val startDateParsed = startDate?.tryParseLocalDateTime()
            ?.orReturnFail { format ->
                return failure(DataValidationErrors.DataFormatMismatch("tender.lot.option.renewal.startDate", format, startDate))
            }

        val endDateParsed = endDate?.tryParseLocalDateTime()
            ?.orReturnFail { format ->
                return failure(DataValidationErrors.DataFormatMismatch("tender.lot.option.renewal.endDate", format, endDate))
            }

        val maxExtentDateParsed = maxExtentDate?.tryParseLocalDateTime()
            ?.orReturnFail { format ->
                return failure(DataValidationErrors.DataFormatMismatch("tender.lot.option.renewal.maxExtentDate", format, maxExtentDate))
            }

        return Period(
            startDate = startDateParsed,
            endDate = endDateParsed,
            maxExtentDate = maxExtentDateParsed,
            durationInDays = durationInDays
        ).asSuccess()
    }

    private fun DivideLotProcess.Request.Payload.Tender.Lot.Option.toDomain(): Result<Option, Fail> =
        Option(
            description = description,
            period = period?.toDomain()?.orForwardFail { return it }
        ).asSuccess()

    private fun DivideLotProcess.Request.Payload.Tender.Lot.Recurrence.toDomain(): Result<RecurrentProcurement, Fail> {
        return RecurrentProcurement(
            description = description,
            dates = dates?.map { date -> date.toDomain().orForwardFail { return it } }
                .orEmpty()
                .let { Periods(it) }

        ).asSuccess()
    }

    private fun DivideLotProcess.Request.Payload.Tender.Lot.Recurrence.Date.toDomain(): Result<Period, Fail> = Period(
        startDate = startDate?.tryParseLocalDateTime()
            ?.orReturnFail { format ->
                return failure(
                    DataValidationErrors.DataFormatMismatch(
                        "tender.lot.option.recurrence.dates.startDate", format, startDate
                    )
                )
            }
    ).asSuccess()

    private fun DivideLotProcess.Request.Payload.Tender.Lot.Option.Period.toDomain(): Result<Period, Fail> {
        val startDateParsed = startDate?.tryParseLocalDateTime()
            ?.orReturnFail { format ->
                return failure(DataValidationErrors.DataFormatMismatch("tender.lot.option.period.startDate", format, startDate))
            }

        val endDateParsed = endDate?.tryParseLocalDateTime()
            ?.orReturnFail { format ->
                return failure(DataValidationErrors.DataFormatMismatch("tender.lot.option.period.endDate", format, endDate))
            }

        val maxExtentDateParsed = maxExtentDate?.tryParseLocalDateTime()
            ?.orReturnFail { format ->
                return failure(DataValidationErrors.DataFormatMismatch("tender.lot.option.period.maxExtentDate", format, maxExtentDate))
            }

        return Period(
            startDate = startDateParsed,
            endDate = endDateParsed,
            maxExtentDate = maxExtentDateParsed,
            durationInDays = durationInDays
        ).asSuccess()
    }

    private fun DivideLotProcess.Request.Payload.Tender.Lot.Value.toDomain(): Value =
        Value(amount = this.amount, currency = this.currency)

    private fun DivideLotProcess.Request.Payload.Tender.Lot.ContractPeriod.toDomain(): Result<Period, Fail> {
        val startDate = this.startDate.tryParseLocalDateTime()
            .orReturnFail { format ->
                return failure(DataValidationErrors.DataFormatMismatch("startDate", format, this.startDate))
            }

        val endDate = this.endDate.tryParseLocalDateTime()
            .orReturnFail { format ->
                return failure(DataValidationErrors.DataFormatMismatch("endDate", format, this.endDate))
            }

        return Period(startDate = startDate, endDate = endDate).asSuccess()
    }

    private fun DivideLotProcess.Request.Payload.Tender.Lot.PlaceOfPerformance.toDomain(): PlaceOfPerformance =
        PlaceOfPerformance(
            description = this.description,
            address = this.address.toDomain()
        )

    private fun DivideLotProcess.Request.Payload.Tender.Lot.PlaceOfPerformance.Address.toDomain(): Address =
        Address(
            streetAddress = this.streetAddress,
            postalCode = this.postalCode,
            addressDetails = this.addressDetails.toDomain()
        )

    private fun DivideLotProcess.Request.Payload.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.toDomain(): AddressDetails =
        AddressDetails(
            country = this.country.toDomain(),
            region = this.region.toDomain(),
            locality = this.locality.toDomain()
        )

    private fun DivideLotProcess.Request.Payload.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Country.toDomain(): CountryDetails =
        CountryDetails(
            scheme = this.scheme,
            id = this.id,
            description = this.description
        )

    private fun DivideLotProcess.Request.Payload.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Region.toDomain(): RegionDetails =
        RegionDetails(
            scheme = this.scheme,
            id = this.id,
            description = this.description
        )

    private fun DivideLotProcess.Request.Payload.Tender.Lot.PlaceOfPerformance.Address.AddressDetails.Locality.toDomain(): LocalityDetails =
        LocalityDetails(
            scheme = this.scheme,
            id = this.id,
            description = this.description
        )

    private fun DivideLotProcess.Request.Payload.Tender.Item.toDomain(): Item =
        Item(
            id = ItemId.create(this.id),
            relatedLot = LotId.create(this.relatedLot)
        )
}
