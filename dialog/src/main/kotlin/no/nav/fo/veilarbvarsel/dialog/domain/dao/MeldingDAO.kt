package no.nav.fo.veilarbvarsel.dialog.domain.dao

import no.nav.fo.veilarbvarsel.dialog.domain.Melding
import no.nav.fo.veilarbvarsel.dialog.domain.VarselStatus
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.joda.time.DateTime

object MeldingDAO: IntIdTable("Melding") {

    val messageId = uuid("message_id").uniqueIndex()
    val recieverNorskIdent = varchar("receiver", length = 30)
    val varselStatus = varchar("varsel_status", 20)
    val varselId = varchar("varsel_id", 30).nullable()

    val created = datetime("created").default(DateTime.now())
    val read = datetime("read").nullable()

    val sending = datetime("send_start").nullable()
    val sent = datetime("send_end").nullable()

    val failed = datetime("failed_time").nullable()
    val errormessage = varchar("failed_message", 255).nullable()

    fun ResultRow.asMelding() = Melding(
        this[MeldingDAO.id].value,
        this[messageId],
        this[recieverNorskIdent],
        VarselStatus.valueOf(this[varselStatus]),
        this[varselId],
        this[created].toLocalDateTime(),
        this[read]?.toLocalDateTime(),
        this[sending]?.toLocalDateTime(),
        this[sent]?.toLocalDateTime(),
        this[failed]?.toLocalDateTime(),
        this[errormessage]
    )

}