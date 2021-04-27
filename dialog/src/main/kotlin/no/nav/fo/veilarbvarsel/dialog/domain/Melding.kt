package no.nav.fo.veilarbvarsel.dialog.domain

import org.joda.time.LocalDateTime
import java.util.*

data class Melding(
    val id: Int,
    val messageId: UUID,
    val receiverNorskIdent: String,
    val varselStatus: VarselStatus,
    val varselId: String?,
    val created: LocalDateTime,
    val read: LocalDateTime?,
    val sending: LocalDateTime?,
    val sent: LocalDateTime?,
    val failed: LocalDateTime?,
    val failedMessage: String?
)
