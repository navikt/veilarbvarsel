package no.nav.fo.veilarbvarsel.varsel

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.routing.*
import no.nav.fo.veilarbvarsel.dabevents.DabEventService

fun Route.varselApi(service: DabEventService) {
    route("/varsel") {

        post("/create") {
            val varsel = call.receive<Varsel>()
            service.createVarsel(varsel)
        }

        post("/done") {
            val done = call.receive<Done>()
            service.createDone(done)
        }
    }
}
