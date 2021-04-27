package no.nav.fo.veilarbvarsel.server.kafka

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import no.nav.fo.veilarbvarsel.server.domain.kafka.internal.CreateVarselPayload
import no.nav.fo.veilarbvarsel.server.domain.kafka.internal.EventType
import no.nav.fo.veilarbvarsel.server.domain.kafka.internal.InternalEvent
import no.nav.fo.veilarbvarsel.server.domain.kafka.internal.Payload
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.joda.time.LocalDateTime
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*

abstract class KafkaInternalConsumer(
    val topic: String
) {

    val props = Properties()
    var running = false
    lateinit var job: Job

    val logger = LoggerFactory.getLogger(this.javaClass)

    init {
        props["bootstrap.servers"] = "localhost:9092"
        props["group.id"] = "account"
        props["key.deserializer"] = StringDeserializer::class.java
        props["value.deserializer"] = StringDeserializer::class.java

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                logger.info("Shutting down Kafka Consumer gracefully...")
                running = false

                while (job.isActive) {
                    sleep(100)
                }

                logger.info("Kafka Consumer has shut down!")
            }
        })
    }

    fun start() {
        running = true

        job = GlobalScope.launch {
            val consumer = KafkaConsumer<String, String>(props).apply {
                subscribe(listOf(topic))
            }

            consumer.use {
                while(running) {
                    val records = consumer.poll(Duration.ofMillis(100))

                    records.iterator().forEach {
                        val data = it.value().toKafkaInternalMessage()
                        if(data.isPresent) {
                            handle(data.get())
                        }
                    }
                }
            }
        }
    }

    fun stop() {
        running = false
    }

    abstract fun handle(data: InternalEvent)

    fun String.toKafkaInternalMessage(): Optional<InternalEvent> {
        val objectMapper = jacksonObjectMapper().apply {
            registerModule(JavaTimeModule())
            registerModule(KotlinModule())
            registerModule(JodaModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }

        val jsonStruct = objectMapper.readTree(this)

        val type = jsonStruct["type"].textValue()
        val event = EventType.valueOf(jsonStruct["event"].textValue())
        var payload: Payload? = null

        if(type.equals("VARSEL")) {
            when(event) {
                EventType.CREATE -> payload = objectMapper.treeToValue(jsonStruct["payload"], CreateVarselPayload::class.java)
                EventType.MODIFY -> TODO()
                EventType.CANCEL -> TODO()
                else -> payload = null
            }
        }

        if(payload != null) {
            return Optional.of(InternalEvent(
                UUID.fromString(jsonStruct["transactionId"].asText()),
                LocalDateTime.parse(jsonStruct["timestamp"].textValue()),
                type,
                event,
                payload
            ))
        } else {
            return Optional.empty()
        }
    }


}