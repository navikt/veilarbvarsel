package no.nav.fo.veilarbvarsel.events.skal_slettes

import no.nav.fo.veilarbvarsel.events.*
import no.nav.fo.veilarbvarsel.varsel.Done
import no.nav.fo.veilarbvarsel.varsel.Varsel
import java.time.LocalDateTime
import java.util.*

class EventService(private val eventProducer: EventProducer) {

    fun createVarsel(varsel: Varsel) {
        eventProducer.send(
            Event(
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
            Event(
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
