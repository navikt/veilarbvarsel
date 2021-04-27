package no.nav.fo.veilarbvarsel.brukernotifikasjon

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.serializers.KafkaAvroSerializer
import no.nav.brukernotifikasjon.schemas.Beskjed
import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.brukernotifikasjon.schemas.builders.BeskjedBuilder
import no.nav.fo.veilarbvarsel.kafka.utils.KafkaCallback
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.joda.time.LocalDateTime
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*

object BrukerNotifikasjonBeskjedProducer {

    val logger = LoggerFactory.getLogger(this.javaClass)

    val topic = System.getenv("KAFKA_DOKNOTIFIKASJON_BESKJED_TOPIC") ?: "aapen-brukernotifikasjon-nyBeskjed-v1-testing"
    val systemUser = System.getenv("SYSTEM_UESER") ?: "VEILARBVARSEL"

    val producer: KafkaProducer<Nokkel, Beskjed>

    init {
        val host = System.getenv("KAFKA_HOST") ?: "localhost"
        val port = System.getenv("KAFKA_PORT") ?: 29092

        val properties = Properties()
        properties[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "$host:$port"
        properties[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = KafkaAvroSerializer::class.java
        properties[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaAvroSerializer::class.java
        properties[AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG] = "http://localhost:8081/"

        producer = KafkaProducer(properties)

        Runtime.getRuntime().addShutdownHook(Thread {
            logger.info("Closing Person Notifikasjon Beskjed Producer...")
            producer.flush()
            producer.close()
            logger.info("Done!")
        })
    }

    fun send(
        varselId: String,
        fodselsnummer: String,
        groupId: String,
        message: String,
        link: URL,
        sikkerhetsnivaa: Int,
        visibleUntil: LocalDateTime,
        callback: KafkaCallback?
    ) {
        val nokkel = Nokkel(systemUser, varselId)

        val beskjed = BeskjedBuilder()
            .withTidspunkt(java.time.LocalDateTime.now())
            .withFodselsnummer(fodselsnummer)
            .withGrupperingsId(groupId)
            .withTekst(message)
            .withLink(link)
            .withSikkerhetsnivaa(sikkerhetsnivaa)
            .withSynligFremTil(
                java.time.LocalDateTime.of(
                    visibleUntil.year,
                    visibleUntil.monthOfYear,
                    visibleUntil.dayOfMonth,
                    visibleUntil.hourOfDay,
                    visibleUntil.minuteOfHour,
                    visibleUntil.secondOfMinute
                )
            )
            .build()

        val record = ProducerRecord(topic, nokkel, beskjed)

        producer.send(record) { _, exception ->
            if (exception == null) {
                callback?.onSuccess()
            } else {
                callback?.onFailure(exception)
            }
        }
    }


}