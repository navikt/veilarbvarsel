package no.nav.fo.veilarbvarsel.dabevents

import no.nav.fo.veilarbvarsel.varsel.domain.Done
import no.nav.fo.veilarbvarsel.varsel.domain.Varsel
import java.time.LocalDateTime
import java.util.*

class DabEventService(private val eventProducer: DabEventProducer) {

    fun createVarsel(varsel: Varsel) {
        eventProducer.send(
            DabEvent(
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
                    varsel.link.toString(),
                    varsel.sikkerhetsnivaa,
                    varsel.visibleUntil,
                    varsel.externalVarsling
                )
            )
        )
    }

    fun createDone(done: Done) {
        eventProducer.send(
            DabEvent(
                UUID.randomUUID(),
                LocalDateTime.now(),
                "VARSEL",
                EventType.DONE,
                DonePayload(
                    done.system,
                    done.id,
                    done.fodselsnummer,
                    done.groupId
                )
            )
        )
    }

}