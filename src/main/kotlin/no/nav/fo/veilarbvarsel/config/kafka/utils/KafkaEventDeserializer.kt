package no.nav.fo.veilarbvarsel.config.kafka.utils

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.fo.veilarbvarsel.varsel.VarselEvent
import org.apache.kafka.common.serialization.Deserializer

class KafkaEventDeserializer : Deserializer<VarselEvent> {

    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        registerModule(KotlinModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    override fun deserialize(topic: String?, data: ByteArray): VarselEvent {
        return objectMapper.readValue(data, VarselEvent::class.java)
    }
}