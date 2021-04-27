package no.nav.fo.veilarbvarsel.server.domain.dao

import no.nav.fo.veilarbvarsel.server.domain.Varsel
import no.nav.fo.veilarbvarsel.server.domain.VarselEvent
import no.nav.fo.veilarbvarsel.server.domain.VarselType
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.joda.time.DateTime
import java.util.*
import java.util.stream.Collectors

object VarselDAO: IntIdTable("VARSEL") {

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

    fun asVarsler(entries: List<ResultRow>): Set<Varsel> {
        val distinctRows = distinctRows(entries)

        return distinctRows.stream()
            .map { it.asVarsel(VarselEventDAO.getEventsForId(it[VarselDAO.id].value, entries)) }
            .collect(Collectors.toUnmodifiableSet())
    }

    private fun distinctRows(entries: List<ResultRow>): Set<ResultRow> {
        val rows = mutableSetOf<ResultRow>()

        entries.forEach { entry ->
            val rowsContainItem = rows.stream().anyMatch { row -> row[VarselDAO.id] == entry[VarselDAO.id] }

            if (!rowsContainItem) {
                rows.add(entry)
            }
        }

        return Collections.unmodifiableSet(rows)
    }

    private fun ResultRow.asVarsel(events: List<VarselEvent>): Varsel {
        return Varsel(
            this[VarselDAO.id].value,
            this[varselId],
            VarselType.valueOf(this[type]),
            this[fodselsnummer],
            this[groupId],
            this[message],
            this[sikkerhetsnivaa],
            this[visibleUntil]?.toLocalDateTime(),
            events
        )
    }
}