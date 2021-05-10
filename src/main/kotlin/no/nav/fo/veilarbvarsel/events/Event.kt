package no.nav.fo.veilarbvarsel.events

import no.nav.fo.veilarbvarsel.varsel.VarselType
import java.time.LocalDateTime
import java.util.*

enum class EventType {
    CREATE,
    CREATED,
    DONE,
    ERROR
}

data class Event(
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

data class DonePayload(
    val system: String,
    val id: String,
    val fodselsnummer: String,
    val groupId: String
) : Payload()
