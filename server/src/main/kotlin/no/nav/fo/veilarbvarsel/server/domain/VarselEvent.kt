package no.nav.fo.veilarbvarsel.server.domain

import org.joda.time.LocalDateTime

data class VarselEvent(
    val id: Int,
    val event: VarselStatus,
    val timestamp: LocalDateTime
)
