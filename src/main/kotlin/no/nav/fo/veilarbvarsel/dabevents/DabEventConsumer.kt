package no.nav.fo.veilarbvarsel.dabevents

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.fo.veilarbvarsel.config.KafkaEnvironment
import no.nav.fo.veilarbvarsel.kafka.KafkaConsumerWrapper
import no.nav.fo.veilarbvarsel.varsel.VarselService
import org.joda.time.LocalDateTime
import org.slf4j.LoggerFactory
import java.util.*

class DabEventConsumer(
    env: KafkaEnvironment,
    systemUser: String,
    topics: List<String>,
    val service: VarselService
) : KafkaConsumerWrapper<String, String>(env, systemUser, topics) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun handle(data: String) {
        val handlableEvent = toDabEvent(data)

        if(handlableEvent.isPresent) {
            val event = handlableEvent.get()
            logger.info("[${event.transactionId}] [${event.event}]: ${event.payload}")

            when (event.payload) {
                is CreateVarselPayload -> {
                    service.create(event.transactionId, event.payload)
                }
                is VarselCreatedPayload -> {
                    logger.info("Handling Varsel Created event $event")
                }
            }

        }
    }

    private fun toDabEvent(data: String): Optional<DabEvent> {
        val objectMapper = jacksonObjectMapper().apply {
            registerModule(JavaTimeModule())
            registerModule(KotlinModule())
            registerModule(JodaModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }

        val jsonStruct = objectMapper.readTree(data)

        val type = jsonStruct["type"].textValue()
        val event = EventType.valueOf(jsonStruct["event"].textValue())
        var payload: Payload? = null

        if (type.equals("VARSEL")) {
            payload = when (event) {
                EventType.CREATE -> objectMapper.treeToValue(jsonStruct["payload"], CreateVarselPayload::class.java)
                EventType.CREATED -> objectMapper.treeToValue(jsonStruct["payload"], VarselCreatedPayload::class.java)
                else -> null
            }
        }

        return if (payload != null) {
            Optional.of(
                DabEvent(
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