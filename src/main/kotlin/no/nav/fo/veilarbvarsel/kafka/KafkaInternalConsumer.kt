package no.nav.fo.veilarbvarsel.kafka

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import no.nav.fo.veilarbvarsel.domain.events.CreateVarselPayload
import no.nav.fo.veilarbvarsel.domain.events.EventType
import no.nav.fo.veilarbvarsel.domain.events.InternalEvent
import no.nav.fo.veilarbvarsel.domain.events.Payload
import no.nav.fo.veilarbvarsel.features.ClosableJob
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.joda.time.LocalDateTime
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*

abstract class KafkaInternalConsumer(val topic: String): ClosableJob {

    val props = Properties()
    var running = false
    lateinit var job: Job

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

        job = GlobalScope.launch {
            val consumer = KafkaConsumer<String, String>(props).apply {
                subscribe(listOf(topic))
            }

            consumer.use {
                while (running) {
                    val records = consumer.poll(Duration.ofMillis(100))

                    records.iterator().forEach {
                        val data = toKafkaInternalMessage(it.value())
                        if (data.isPresent) {
                            handle(data.get())
                        }
                    }
                }
            }
        }
    }

    override fun close() {
        if (job == null) {
            return
        }

        running = false
        while (job.isActive) {
            Thread.sleep(100)
        }
    }

    abstract fun handle(data: InternalEvent)

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