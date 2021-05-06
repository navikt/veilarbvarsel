package no.nav.fo.veilarbvarsel.varsel

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.routing.*
import no.nav.fo.veilarbvarsel.dabevents.DabEventService
import no.nav.fo.veilarbvarsel.varsel.domain.Done
import no.nav.fo.veilarbvarsel.varsel.domain.DoneDto
import no.nav.fo.veilarbvarsel.varsel.domain.Varsel
import no.nav.fo.veilarbvarsel.varsel.domain.VarselDto
import java.net.URL

fun Route.varselApi(service: DabEventService) {
    route("/varsel") {

        post("/create") {
            val dto = call.receive<VarselDto>()
            service.createVarsel(dto.asVarsel())
        }

        post("/done") {
            val dto = call.receive<DoneDto>()
            service.createDone(dto.asDone())
        }
    }
}

private fun VarselDto.asVarsel(): Varsel {
    return Varsel(
        system,
        id,
        type,
        fodselsnummer,
        groupId,
        URL(link),
        message,
        sikkerhetsnivaa,
        visibleUntil,
        externalVarsling
    )
}

private fun DoneDto.asDone(): Done {
    return Done(id, system, fodselsnummer, groupId)
}