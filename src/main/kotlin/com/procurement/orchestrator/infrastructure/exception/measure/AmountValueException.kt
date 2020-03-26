package com.procurement.orchestrator.infrastructure.exception.measure

class AmountValueException(amount: String, description: String = "") :
    RuntimeException("Incorrect value of the amount: '$amount'. $description")
