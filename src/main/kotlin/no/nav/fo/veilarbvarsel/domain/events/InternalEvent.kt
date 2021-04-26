package no.nav.fo.veilarbvarsel.domain.events

import no.nav.fo.veilarbvarsel.domain.VarselType
import org.joda.time.LocalDateTime
import java.util.*

enum class EventType {
    CREATE,
    CREATED,
    MODIFY,
    MODIFIED,
    CANCEL,
    CANCELED,
    ERROR
}

data class InternalEvent(
    val transactionId: UUID,
    val timestamp: LocalDateTime,
    val type: String,
    val event: EventType,
    val payload: Payload
)

sealed class Payload

data class CreateVarselPayload(
    val varselId: String,
    val varselType: VarselType,
    val fodselsnummer: String,
    val groupId: String,
    val message: String,
    val sikkerhetsnivaa: Int,
    val visibleUntil: LocalDateTime?
) : Payload()