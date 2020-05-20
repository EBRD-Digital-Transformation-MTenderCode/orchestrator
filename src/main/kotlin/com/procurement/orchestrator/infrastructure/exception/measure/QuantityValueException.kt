package com.procurement.orchestrator.infrastructure.exception.measure

class QuantityValueException(quantity: String, description: String = "") :
    RuntimeException("Incorrect value of the quantity: '$quantity'. $description")
