package no.nav.fo.veilarbvarsel.server.domain.dao

import no.nav.fo.veilarbvarsel.server.domain.VarselEvent
import no.nav.fo.veilarbvarsel.server.domain.VarselStatus
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import java.util.stream.Collectors

object VarselEventDAO : IntIdTable("VARSEL_EVENT") {

    val varselId = reference("varsel_id", VarselDAO)
    val event = varchar("event", 20)
    val timestamp = datetime("timestamp")

    fun getEventsForId(id: Int, entries: List<ResultRow>): List<VarselEvent> {
        return entries.stream()
            .filter { it[varselId].value == id }
            .map { it.asVarselEvent() }
            .collect(Collectors.toList())

    }

    private fun ResultRow.asVarselEvent(): VarselEvent {
        return VarselEvent(
            this[VarselEventDAO.id].value,
            VarselStatus.valueOf(this[event]),
            this[timestamp].toLocalDateTime()
        )
    }
}