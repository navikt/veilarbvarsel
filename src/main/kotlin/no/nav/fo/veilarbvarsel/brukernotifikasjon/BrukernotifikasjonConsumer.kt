package no.nav.fo.veilarbvarsel.kafka.doknotifikasjon

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.doknotifikasjon.schemas.Doknotifikasjon
import no.nav.fo.veilarbvarsel.features.ClosableJob
import no.nav.fo.veilarbvarsel.varsel.VarselService
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.LoggerFactory
import java.util.*

class BrukerNotifikasjonConsumer(val service: VarselService): ClosableJob {

    private val properties = Properties()
    private val topic = System.getenv("KAFKA_EXTERNAL_RECIPT_TOPIC")?: "TEST_EXTERNAL_RECIPT_TOPIC"

    private var shutdown = false
    private var running = false

    private val logger = LoggerFactory.getLogger(this.javaClass)

    init {
        val host = System.getenv("KAFKA_HOST") ?: "localhost"
        val port = System.getenv("KAFKA_PORT") ?: 29092

        properties["bootstrap.servers"] = "$host:$port"
        properties["group.id"] = System.getenv("SERVICE_NAME") ?: "VARSEL_SERVICE"
        properties["key.deserializer"] = KafkaAvroDeserializer::class.java
        properties["value.deserializer"] = KafkaAvroDeserializer::class.java
        properties[AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG] = "http://localhost:8081/"
    }

    override fun run() {
        logger.info("Starting Brukernotifikasjon Recipt consumer")
        running = true

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                close()
            }
        })

        val consumer = KafkaConsumer<String, Doknotifikasjon>(properties).apply {
            subscribe(listOf(topic))
        }

        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }
}