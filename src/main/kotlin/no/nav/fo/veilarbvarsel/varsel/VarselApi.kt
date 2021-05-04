package no.nav.fo.veilarbvarsel.varsel

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.routing.*
import no.nav.fo.veilarbvarsel.dabevents.DabEventService
import no.nav.fo.veilarbvarsel.varsel.domain.Varsel
import no.nav.fo.veilarbvarsel.varsel.domain.VarselDto

fun Route.varselApi(service: DabEventService) {
    route("/varsel") {

        post {
            val dto = call.receive<VarselDto>()
            service.createVarsel(dto.asVarsel())
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
            link,
            message,
            sikkerhetsnivaa,
            visibleUntil,
            externalVarsling
    )
}