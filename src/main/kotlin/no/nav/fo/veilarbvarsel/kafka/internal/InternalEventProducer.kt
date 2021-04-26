package no.nav.fo.veilarbvarsel.kafka.internal

import no.nav.fo.veilarbvarsel.domain.Varsel
import no.nav.fo.veilarbvarsel.domain.VarselType
import no.nav.fo.veilarbvarsel.domain.events.CreateVarselPayload
import no.nav.fo.veilarbvarsel.domain.events.EventType
import no.nav.fo.veilarbvarsel.domain.events.InternalEvent
import no.nav.fo.veilarbvarsel.domain.events.VarselCreatedPayload
import no.nav.fo.veilarbvarsel.kafka.KafkaProducer
import no.nav.fo.veilarbvarsel.kafka.utils.KafkaCallback
import org.joda.time.LocalDateTime
import java.util.*

object InternalEventProducer {

    val internalTopic = System.getenv("KAFKA_INTERNAL_TOPIC")?: "TEST_INTERNAL_TOPIC"

    val producer = KafkaProducer<String, InternalEvent>()

    fun createVarsel(
        transactionId: UUID?,
        varselId: String,
        type: VarselType,
        fodselsnummer: String,
        groupId: String,
        message: String,
        sikkerhetsnivaa: Int,
        visibleUntil: LocalDateTime?,
        callback: KafkaCallback?
    ) {
        val event = InternalEvent(
            transactionId?: UUID.randomUUID(),
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

        producer.send(internalTopic, "VARSEL", event, callback)
    }

    fun varselCreated(transactionId: UUID?, varsel: Varsel, callback: KafkaCallback?) {
        val event = InternalEvent(
            transactionId?: UUID.randomUUID(),
            LocalDateTime.now(),
            "VARSEL",
            EventType.CREATED,
            VarselCreatedPayload(
                varsel.varselId,
                varsel.type,
                varsel.fodselsnummer,
                varsel.groupId,
                varsel.message,
                varsel.sikkerhetsnivaa,
                varsel.visibleUntil
            )
        )

        producer.send(internalTopic, "VARSEL", event, callback)
    }

}