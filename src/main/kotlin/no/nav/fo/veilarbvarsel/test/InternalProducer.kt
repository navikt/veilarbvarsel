package no.nav.fo.veilarbvarsel.test

import no.nav.fo.veilarbvarsel.domain.VarselType
import no.nav.fo.veilarbvarsel.domain.events.CreateVarselPayload
import no.nav.fo.veilarbvarsel.domain.events.EventType
import no.nav.fo.veilarbvarsel.domain.events.InternalEvent
import no.nav.fo.veilarbvarsel.kafka.KafkaProducer
import no.nav.fo.veilarbvarsel.kafka.utils.KafkaCallback
import org.joda.time.LocalDateTime
import org.slf4j.LoggerFactory
import java.util.*

object InternalProducer {

    val internalTopic = System.getenv("KAFKA_INTERNAL_TOPIC")?: "TEST_INTERNAL_TOPIC"

    val logger = LoggerFactory.getLogger(this.javaClass)
    val producer = KafkaProducer<String, InternalEvent>()

    fun createVarsel(
        varselId: String,
        type: VarselType,
        fodselsnummer: String,
        groupId: String,
        message: String,
        sikkerhetsnivaa: Int,
        visibleUntil: LocalDateTime?
    ) {
        val event = InternalEvent(
            UUID.randomUUID(),
            LocalDateTime.now(),
            "VARSEL",
            EventType.CREATE,
            CreateVarselPayload(
                varselId,
                type,
                fodselsnummer,
                groupId,
                message,
                sikkerhetsnivaa,
                visibleUntil
            )
        )

        producer.send(internalTopic, "VARSEL", event, object: KafkaCallback {
            override fun onSuccess() {
                logger.info("[Transaction id: ${event.transactionId}] Sent a create message with id $varselId")
            }
        })
    }

}