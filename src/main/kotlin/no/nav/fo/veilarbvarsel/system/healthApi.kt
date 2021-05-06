package no.nav.fo.veilarbvarsel.system

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.micrometer.prometheus.PrometheusMeterRegistry

fun Route.healthApi(collectorRegistry: PrometheusMeterRegistry) {

    get("/ping") {
        call.respondText("""{"ping": "pong"}""", ContentType.Application.Json)
    }

    get("/isAlive") {
        call.respondText(text = "ALIVE", contentType = ContentType.Text.Plain)
    }

    get("/isReady") {
        call.respondText(text = "READY", contentType = ContentType.Text.Plain)
    }

    get("/metrics") {
        call.respond(collectorRegistry.scrape())
    }

}