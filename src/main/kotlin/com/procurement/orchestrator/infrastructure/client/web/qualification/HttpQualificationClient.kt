package com.procurement.orchestrator.infrastructure.client.web.qualification

import com.procurement.orchestrator.application.client.QualificationClient
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import java.net.URL

class HttpQualificationClient(private val webClient: WebClient, properties: ComponentProperties.Component) :
    QualificationClient {

    private val url: URL = URL(properties.url + "/command2")

}
