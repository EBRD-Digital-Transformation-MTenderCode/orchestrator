package com.procurement.orchestrator.domain.model.organization.datail

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.organization.datail.account.BankAccount
import com.procurement.orchestrator.domain.model.organization.datail.legalform.LegalForm
import com.procurement.orchestrator.domain.model.organization.datail.permit.Permit
import java.io.Serializable

data class Details(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("typeOfBuyer") @param:JsonProperty("typeOfBuyer") val typeOfBuyer: TypeOfBuyer? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("typeOfSupplier") @param:JsonProperty("typeOfSupplier") val typeOfSupplier: TypeOfSupplier? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("mainEconomicActivities") @param:JsonProperty("mainEconomicActivities") val mainEconomicActivities: List<String> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("mainGeneralActivity") @param:JsonProperty("mainGeneralActivity") val mainGeneralActivity: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("mainSectoralActivity") @param:JsonProperty("mainSectoralActivity") val mainSectoralActivity: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("permits") @param:JsonProperty("permits") val permits: List<Permit> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("bankAccounts") @param:JsonProperty("bankAccounts") val bankAccounts: List<BankAccount> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("legalForm") @param:JsonProperty("legalForm") val legalForm: LegalForm? = null,

    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("isACentralPurchasingBody") @param:JsonProperty("isACentralPurchasingBody") val isACentralPurchasingBody: Boolean? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("nutsCode") @param:JsonProperty("nutsCode") val nutsCode: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("scale") @param:JsonProperty("scale") val scale: Scale? = null
) : Serializable
