package no.nav.fo.veilarbvarsel.varsel.domain

data class DoneDto(
    val id: String,
    val system: String,
    val fodselsnummer: String,
    val groupId: String,
)
