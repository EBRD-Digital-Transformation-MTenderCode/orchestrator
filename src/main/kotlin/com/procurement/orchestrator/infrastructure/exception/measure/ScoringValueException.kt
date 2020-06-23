package com.procurement.orchestrator.infrastructure.exception.measure

class ScoringValueException(scoring: String, description: String = "") :
    RuntimeException("Incorrect value of the scoring: '$scoring'. $description")
