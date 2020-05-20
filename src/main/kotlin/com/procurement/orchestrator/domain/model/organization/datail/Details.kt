package com.procurement.orchestrator.domain.model.organization.datail

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.organization.datail.account.BankAccounts
import com.procurement.orchestrator.domain.model.organization.datail.legalform.LegalForm
import com.procurement.orchestrator.domain.model.organization.datail.permit.Permits
import com.procurement.orchestrator.domain.model.updateBy
import java.io.Serializable

data class Details(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("typeOfBuyer") @param:JsonProperty("typeOfBuyer") val typeOfBuyer: TypeOfBuyer? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("typeOfSupplier") @param:JsonProperty("typeOfSupplier") val typeOfSupplier: TypeOfSupplier? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("mainEconomicActivities") @param:JsonProperty("mainEconomicActivities") val mainEconomicActivities: MainEconomicActivities = MainEconomicActivities(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("mainGeneralActivity") @param:JsonProperty("mainGeneralActivity") val mainGeneralActivity: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("mainSectoralActivity") @param:JsonProperty("mainSectoralActivity") val mainSectoralActivity: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("permits") @param:JsonProperty("permits") val permits: Permits = Permits(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("bankAccounts") @param:JsonProperty("bankAccounts") val bankAccounts: BankAccounts = BankAccounts(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("legalForm") @param:JsonProperty("legalForm") val legalForm: LegalForm? = null,

    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("isACentralPurchasingBody") @param:JsonProperty("isACentralPurchasingBody") val isACentralPurchasingBody: Boolean? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("nutsCode") @param:JsonProperty("nutsCode") val nutsCode: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("scale") @param:JsonProperty("scale") val scale: Scale? = null
) : ComplexObject<Details>, Serializable {

    override fun updateBy(src: Details) = Details(
        typeOfBuyer = src.typeOfBuyer or typeOfBuyer,
        typeOfSupplier = src.typeOfSupplier or typeOfSupplier,
        mainEconomicActivities = mainEconomicActivities combineBy src.mainEconomicActivities,
        mainGeneralActivity = src.mainGeneralActivity or mainGeneralActivity,
        mainSectoralActivity = src.mainSectoralActivity or mainSectoralActivity,
        permits = permits updateBy src.permits,
        bankAccounts = bankAccounts combineBy src.bankAccounts,
        legalForm = legalForm updateBy src.legalForm,
        isACentralPurchasingBody = src.isACentralPurchasingBody or isACentralPurchasingBody,
        nutsCode = src.nutsCode or nutsCode,
        scale = src.scale or scale
    )
}
