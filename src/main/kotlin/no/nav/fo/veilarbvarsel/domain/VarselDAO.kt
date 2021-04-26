package no.nav.fo.veilarbvarsel.domain

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.joda.time.DateTime

object VarselDAO : IntIdTable("VARSEL") {
    val varselId = varchar("varsel_id", 50).uniqueIndex()
    val type = varchar("type", 20)
    val fodselsnummer = varchar("fodselsnummer", 20)
    val groupId = varchar("group_id", 30)
    val message = varchar("message", 255)
    val sikkerhetsnivaa = integer("sikkerhetsnivaa")
    val visibleUntil = datetime("visible_until").nullable()
    val status = varchar("status", 50)
    val received = datetime("received_date").default(DateTime.now())
    val sending = datetime("sending_date").nullable()
    val sent = datetime("sent_date").nullable()
    val canceled = datetime("canceled_date").nullable()
    val failed = datetime("failed_date").nullable()
    val comment = varchar("comment", 255).nullable()

    fun ResultRow.asVarsel(): Varsel {
        return Varsel(
            this[id].value,
            this[varselId],
            VarselType.valueOf(this[type]),
            this[fodselsnummer],
            this[groupId],
            this[message],
            this[sikkerhetsnivaa],
            this[visibleUntil]?.toLocalDateTime(),
            VarselStatus.valueOf(this[status]),
            this[received].toLocalDateTime(),
            this[sending]?.toLocalDateTime(),
            this[sent]?.toLocalDateTime(),
            this[canceled]?.toLocalDateTime(),
            this[failed]?.toLocalDateTime(),
            this[comment]
        )
    }
}