package no.nav.fo.veilarbvarsel.varsel.domain

import java.time.LocalDateTime


/**
 * Compbination of system+id has to be unique
 */
data class VarselDto(
    val id: String,
    val system: String,
    val type: VarselType,
    val fodselsnummer: String,
    val sikkerhetsnivaa: Int,
    val groupId: String,
    val link: String,
    val message: String,
    val externalVarsling: Boolean,
    val visibleUntil: LocalDateTime?,
)