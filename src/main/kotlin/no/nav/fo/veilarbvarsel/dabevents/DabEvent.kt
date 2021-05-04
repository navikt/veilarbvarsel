package no.nav.fo.veilarbvarsel.dabevents

import no.nav.fo.veilarbvarsel.varsel.domain.VarselType
import org.joda.time.LocalDateTime
import java.util.*

enum class EventType {
    CREATE,
    CREATED,
    MODIFY,
    MODIFIED,
    CANCEL,
    CANCELED,
    FINISH,
    FINISHED,
    ERROR
}

data class DabEvent(
    val transactionId: UUID,
    val timestamp: LocalDateTime,
    val type: String,
    val event: EventType,
    val payload: Payload
)

sealed class Payload

data class CreateVarselPayload(
    val system: String,
    val id: String,
    val varselType: VarselType,
    val fodselsnummer: String,
    val groupId: String,
    val message: String,
    val link: String,
    val sikkerhetsnivaa: Int,
    val visibleUntil: LocalDateTime?,
    val externalVarsling: Boolean
) : Payload()

data class VarselCreatedPayload(
    val system: String,
    val id: String
) : Payload()