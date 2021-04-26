package no.nav.fo.veilarbvarsel.domain

import org.joda.time.LocalDateTime

enum class VarselType {
    MELDING,
    OPPGAVE
}

enum class VarselStatus {
    RECEIVED,
    SENDING,
    SENT,
    CANCELED,
    FAILED
}

data class Varsel(
    val id: Int,
    val varselId: String,
    val type: VarselType,
    val fodselsnummer: String,
    val groupId: String,
    val message: String,
    val sikkerhetsnivaa: Int,
    val visibleUntil: LocalDateTime?,
    val status: VarselStatus,
    val received: LocalDateTime,
    val sending: LocalDateTime?,
    val sent: LocalDateTime?,
    val canceled: LocalDateTime?,
    val failed: LocalDateTime?,
    val comment: String?
    )