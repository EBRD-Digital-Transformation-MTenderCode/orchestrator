package com.procurement.orchestrator.infrastructure.configuration.property.kafka

import org.apache.kafka.common.config.SslConfigs
import java.util.*

class Ssl(
    /**
     * Password of the private key in the key store file.
     */
    var keyPassword: String? = null,

    /**
     * Location of the key store file.
     */
    var keystoreLocation: String? = null,

    /**
     * Store password for the key store file.
     */
    var keystorePassword: String? = null,

    /**
     * Type of the key store.
     */
    var keyStoreType: String? = null,

    /**
     * Location of the trust store file.
     */
    var truststoreLocation: String? = null,

    /**
     * Store password for the trust store file.
     */
    var truststorePassword: String? = null,

    /**
     * Type of the trust store.
     */
    var trustStoreType: String? = null,

    /**
     * SSL protocol to use.
     */
    var protocol: String? = null
) {
    fun buildProperties(): Properties = Properties()
        .apply {
            keyPassword?.also { this[SslConfigs.SSL_KEY_PASSWORD_CONFIG] = it }
            keystoreLocation?.also { this[SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG] = it }
            keystorePassword?.also { this[SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG] = it }
            keyStoreType?.also { this[SslConfigs.SSL_KEYSTORE_TYPE_CONFIG] = it }
            truststoreLocation?.also { this[SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG] = it }
            truststorePassword?.also { this[SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG] = it }
            trustStoreType?.also { this[SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG] = it }
            protocol?.also { this[SslConfigs.SSL_PROTOCOL_CONFIG] = it }
        }
}
