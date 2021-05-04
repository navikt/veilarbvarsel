package no.nav.fo.veilarbvarsel.varsel.domain

import org.joda.time.LocalDateTime

enum class VarselType {
    BESKJED,
    OPPGAVE
}

data class Varsel(
    val system: String,
    val id: String,
    val type: VarselType,
    val fodselsnummer: String,
    val groupId: String,
    val link: String,
    val message: String,
    val sikkerhetsnivaa: Int,
    val visibleUntil: LocalDateTime?,
    val externalVarsling: Boolean,
)