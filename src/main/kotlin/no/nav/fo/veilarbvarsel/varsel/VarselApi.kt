package no.nav.fo.veilarbvarsel.events.skal_slettes

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.routing.*
import no.nav.fo.veilarbvarsel.varsel.VarselEventProducer
import no.nav.fo.veilarbvarsel.varsel.CreateVarselVarselEvent
import no.nav.fo.veilarbvarsel.varsel.Done
import no.nav.fo.veilarbvarsel.varsel.DoneVarselEvent
import no.nav.fo.veilarbvarsel.varsel.Varsel
import java.time.LocalDateTime
import java.util.*

fun Route.varselApi(varselEventProducer: VarselEventProducer) {
    route("/varsel") {

        post("/create") {
            val varsel = call.receive<Varsel>()
            varselEventProducer.send(
                CreateVarselVarselEvent(
                    UUID.randomUUID(),
                    LocalDateTime.now(),
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
        }

        post("/done") {
            val done = call.receive<Done>()
            varselEventProducer.send(
                DoneVarselEvent(
                    UUID.randomUUID(),
                    LocalDateTime.now(),
                    done.system,
                    done.id,
                    done.fodselsnummer,
                    done.groupId
                )
            )
        }
    }
}
