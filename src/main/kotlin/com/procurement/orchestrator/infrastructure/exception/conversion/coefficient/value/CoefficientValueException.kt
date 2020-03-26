package com.procurement.orchestrator.infrastructure.exception.conversion.coefficient.value

class CoefficientValueException(coefficientValue: String, description: String = "") :
    RuntimeException("Incorrect value of the coefficient: '$coefficientValue'. $description")
