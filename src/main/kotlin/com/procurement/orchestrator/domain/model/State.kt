package com.procurement.orchestrator.domain.model

import com.procurement.orchestrator.domain.EnumElementProvider

class State<S, SD>(val status: S?, val statusDetails: SD?) where S : Enum<S>,
                                                                 S : EnumElementProvider.Key,
                                                                 SD : Enum<SD>,
                                                                 SD : EnumElementProvider.Key
