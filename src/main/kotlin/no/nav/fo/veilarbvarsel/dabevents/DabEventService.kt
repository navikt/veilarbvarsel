package no.nav.fo.veilarbvarsel.dabevents

import no.nav.fo.veilarbvarsel.varsel.domain.Varsel
import org.joda.time.LocalDateTime
import java.util.*

class DabEventService(private val eventProducer: DabEventProducer) {

    fun createVarsel(varsel: Varsel) {
        eventProducer.send(DabEvent(
                UUID.randomUUID(),
                LocalDateTime.now(),
                "VARSEL",
                EventType.CREATE,
                CreateVarselPayload(
                        varsel.system,
                        varsel.id,
                        varsel.type,
                        varsel.fodselsnummer,
                        varsel.groupId,
                        varsel.message,
                        varsel.link,
                        varsel.sikkerhetsnivaa,
                        varsel.visibleUntil,
                        varsel.externalVarsling
                )
        ))
    }

}