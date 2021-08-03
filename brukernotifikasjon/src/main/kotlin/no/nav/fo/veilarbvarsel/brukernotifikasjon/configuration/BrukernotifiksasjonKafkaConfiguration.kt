package no.nav.fo.veilarbvarsel.brukernotifikasjon.configuration

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.serializers.KafkaAvroSerializer
import no.nav.brukernotifikasjon.schemas.Beskjed
import no.nav.brukernotifikasjon.schemas.Done
import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.brukernotifikasjon.schemas.Oppgave
import no.nav.common.kafka.util.KafkaPropertiesPreset
import no.nav.common.utils.NaisUtils
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@EnableKafka
@Configuration
class BrukernotifiksasjonKafkaConfiguration {

    @Value("\${spring.kafka.consumer.group-id}")
    private lateinit var groupId: String

    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var brokers: String

    @Value("\${spring.kafka.schema-registry}")
    private lateinit var schemaRegistry: String

    @Bean
    fun beskjedProducerFactory(): ProducerFactory<Nokkel, Beskjed> {
        return DefaultKafkaProducerFactory(avroProperties())
    }

    @Bean
    fun beskjedKafkaTemplate(): KafkaTemplate<Nokkel, Beskjed> {
        return KafkaTemplate(beskjedProducerFactory())
    }

    @Bean
    fun oppgaveProducerFactory(): ProducerFactory<Nokkel, Oppgave> {
        return DefaultKafkaProducerFactory(avroProperties())
    }

    @Bean
    fun oppgaveKafkaTemplate(): KafkaTemplate<Nokkel, Oppgave> {
        return KafkaTemplate(oppgaveProducerFactory())
    }

    @Bean
    fun doneProducerFactory(): ProducerFactory<Nokkel, Done> {
        return DefaultKafkaProducerFactory(avroProperties())
    }

    @Bean
    fun doneKafkaTemplate(): KafkaTemplate<Nokkel, Done> {
        return KafkaTemplate(doneProducerFactory())
    }


    private fun avroProperties(): Map<String, Any> {
        val credentials = NaisUtils.getCredentials("service_user")
        val properties = KafkaPropertiesPreset.onPremDefaultProducerProperties("VEILARBVARSEL", brokers, credentials)

        properties[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = KafkaAvroSerializer::class.java
        properties[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaAvroSerializer::class.java
        properties[AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG] = schemaRegistry

        return properties.map { it.key.toString() to it.value }.toMap()

    }
}
