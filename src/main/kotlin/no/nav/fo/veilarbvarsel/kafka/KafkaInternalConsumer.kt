package no.nav.fo.veilarbvarsel.kafka

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.fo.veilarbvarsel.domain.events.CreateVarselPayload
import no.nav.fo.veilarbvarsel.domain.events.EventType
import no.nav.fo.veilarbvarsel.domain.events.InternalEvent
import no.nav.fo.veilarbvarsel.domain.events.Payload
import no.nav.fo.veilarbvarsel.features.ClosableJob
import no.nav.fo.veilarbvarsel.varsel.VarselService
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.joda.time.LocalDateTime
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*

class KafkaInternalConsumer(val service: VarselService): ClosableJob {

    val props = Properties()
    val topic = System.getenv("KAFKA_INTERNAL_TOPIC")?: "TEST_INTERNAL_TOPIC"

    var shutdown = false
    var running = false

    val logger = LoggerFactory.getLogger(this.javaClass)

    init {
        val host = System.getenv("KAFKA_HOST") ?: "localhost"
        val port = System.getenv("KAFKA_PORT") ?: 9092

        props["bootstrap.servers"] = "$host:$port"
        props["group.id"] = System.getenv("SERVICE_NAME") ?: "VARSEL_SERVICE"
        props["key.deserializer"] = StringDeserializer::class.java
        props["value.deserializer"] = StringDeserializer::class.java
    }

    override fun run() {
        logger.info("Starting Kafka Internal Consumer")
        running = true

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                close()
            }
        })


        val consumer = KafkaConsumer<String, String>(props).apply {
            subscribe(listOf(topic))
        }

        consumer.use {
            while (!shutdown) {
                val records = consumer.poll(Duration.ofMillis(100))

                records.iterator().forEach {
                    val data = toKafkaInternalMessage(it.value())
                    if (data.isPresent) {
                        handle(data.get())
                    }
                }
            }
        }

        running = false
    }

    override fun close() {
        logger.info("Closing Kafka Internal Consumer...")
        shutdown = true

        while (running) {
            Thread.sleep(100)
        }
        logger.info("Kafka Internal Consumer Closed!")
    }

    fun handle(data: InternalEvent) {
        when (data.payload) {
            is CreateVarselPayload -> service.add(
                data.payload.varselId,
                data.payload.varselType,
                data.payload.fodselsnummer,
                data.payload.groupId,
                data.payload.message,
                data.payload.sikkerhetsnivaa,
                data.payload.visibleUntil
            )
            else -> TODO("Not yet implemented")
        }

    }

    private fun toKafkaInternalMessage(message: String): Optional<InternalEvent> {
        val objectMapper = jacksonObjectMapper().apply {
            registerModule(JavaTimeModule())
            registerModule(KotlinModule())
            registerModule(JodaModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }

        val jsonStruct = objectMapper.readTree(message)

        val type = jsonStruct["type"].textValue()
        val event = EventType.valueOf(jsonStruct["event"].textValue())
        var payload: Payload? = null

        if (type.equals("VARSEL")) {
            payload = when (event) {
                EventType.CREATE -> objectMapper.treeToValue(jsonStruct["payload"], CreateVarselPayload::class.java)
                EventType.CANCEL -> TODO()
                else -> null
            }
        }

        return if (payload != null) {
            Optional.of(
                InternalEvent(
                    UUID.fromString(jsonStruct["transactionId"].asText()),
                    LocalDateTime.parse(jsonStruct["timestamp"].textValue()),
                    type,
                    event,
                    payload
                )
            )
        } else {
            Optional.empty()
        }
    }


}