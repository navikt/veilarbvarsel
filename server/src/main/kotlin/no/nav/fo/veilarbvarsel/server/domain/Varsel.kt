package no.nav.fo.veilarbvarsel.server.domain

import org.joda.time.LocalDateTime


data class Varsel(
    val id: Int?,
    val varselId: String,
    val type: VarselType,
    val fodselsnummer: String,
    val groupId: String,
    val message: String,
    val sikkerhetsnivaa: Int,
    val visibleUntil: LocalDateTime?,
    val status: VarselStatus,
    val received: LocalDateTime,
    val sending: LocalDateTime,
    val sent: LocalDateTime,
    val canceled: LocalDateTime

) {
    constructor(
        varselId: String,
        type: VarselType,
        fodselsnummer: String,
        groupId: String,
        message: String,
        sikkerhetsnivaa: Int,
        visibleUntil: LocalDateTime?,
    ) : this(
        null,
        varselId,
        type,
        fodselsnummer,
        groupId,
        message,
        sikkerhetsnivaa,
        visibleUntil,
        arrayListOf())

    fun isCanceled(): Boolean {
        return this.events.any { it.event == VarselStatus.CANCELED }
    }

    fun isSendingOrSent(): Boolean {
        return this.events.any { (it.event == VarselStatus.SENDING) or (it.event == VarselStatus.SENT) }
    }
}
