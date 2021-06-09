package no.nav.fo.veilarbvarsel.config.kafka.utils.props

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.serializers.KafkaAvroSerializer
import no.nav.common.kafka.util.KafkaPropertiesPreset.onPremDefaultProducerProperties
import no.nav.common.utils.NaisUtils
import no.nav.fo.veilarbvarsel.config.KafkaEnvironment
import org.apache.kafka.clients.producer.ProducerConfig
import java.util.*

data class KafkaAvroProducerProperties(val env: KafkaEnvironment) {

    fun getProperties(): Properties {
        val credentials = NaisUtils.getCredentials("service_user")
        val properties = onPremDefaultProducerProperties("VEILARBVARSEL", env.bootstrapServers, credentials)

        //properties[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = env.bootstrapServers
        properties[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = KafkaAvroSerializer::class.java
        properties[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaAvroSerializer::class.java
        properties[AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG] = env.schemaRegistryUrl

        return properties
    }

}
