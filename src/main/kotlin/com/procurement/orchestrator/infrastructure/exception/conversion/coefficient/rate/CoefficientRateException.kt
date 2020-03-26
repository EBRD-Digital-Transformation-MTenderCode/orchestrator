package com.procurement.orchestrator.infrastructure.exception.conversion.coefficient.rate

class CoefficientRateException(coefficient: String, description: String = "") :
    RuntimeException("Incorrect coefficient: '$coefficient'. $description")
