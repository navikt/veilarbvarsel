package no.nav.fo.veilarbvarsel.events.skal_slettes

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.routing.*
import no.nav.fo.veilarbvarsel.varsel.Done
import no.nav.fo.veilarbvarsel.varsel.Varsel

//TODO slett meg når alt virker
fun Route.varselApi(service: EventService) {
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
