package no.nav.fo.veilarbvarsel.varsel.kafka

import no.nav.fo.veilarbvarsel.varsel.events.VarselEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class VarselProducer(
    val kafkaTemplate: KafkaTemplate<String, VarselEvent>
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun send(topic: String, payload: VarselEvent) {
        logger.info("Sending payload=$payload to topic $topic")
        kafkaTemplate.send(topic, payload)
    }
}
