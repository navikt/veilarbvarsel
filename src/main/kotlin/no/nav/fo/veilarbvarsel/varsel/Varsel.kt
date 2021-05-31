package no.nav.fo.veilarbvarsel.varsel

import java.net.URL
import java.time.LocalDateTime

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
    val link: URL,
    val message: String,
    val sikkerhetsnivaa: Int,
    val visibleUntil: LocalDateTime?,
    val externalVarsling: Boolean,
) {

    fun getSystemId(): String {
        return "$system:::$id"
    }

}
