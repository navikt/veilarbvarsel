package no.nav.fo.veilarbvarsel.domain.kafka

import java.time.LocalDateTime
import java.util.*

abstract class KafkaInternalMessage(
    val transactionId: UUID,
    val timestamp: LocalDateTime,
    val type: String,
    val event: EventType,
    val oppfolgingsPeriodeId: UUID,
    val subject: User,
    val actor: User,
    val data: MessageData
) {

}

interface MessageData {

}
