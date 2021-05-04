package no.nav.fo.veilarbvarsel.kafka.utils.props

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.serializers.KafkaAvroSerializer
import no.nav.fo.veilarbvarsel.config.KafkaEnvironment
import org.apache.kafka.clients.producer.ProducerConfig
import java.util.*

data class KafkaAvroProducerProperties(val env: KafkaEnvironment) {

    fun getProperties(): Properties {
        val properties = Properties()

        properties[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "${env.host}:${env.port}"
        properties[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = KafkaAvroSerializer::class.java
        properties[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaAvroSerializer::class.java
        properties[AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG] = env.schemaRegistryUrl

        return properties
    }

}