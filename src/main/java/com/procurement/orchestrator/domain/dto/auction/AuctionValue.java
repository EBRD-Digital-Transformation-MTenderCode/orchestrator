package com.procurement.orchestrator.domain.dto.auction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuctionValue {

    @JsonProperty(value = "amount")
    private BigDecimal amount;

    public AuctionValue(@JsonProperty("amount") final BigDecimal amount) {
        this.amount = amount;
    }
}
