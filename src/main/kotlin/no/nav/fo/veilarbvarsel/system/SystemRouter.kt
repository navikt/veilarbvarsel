package no.nav.fo.veilarbvarsel.system

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.systemRouter() {
    route("/system") {

        get ("/ping") {
            call.respond(HttpStatusCode.OK, "pong")
        }

        get ( "/isAlive" ) {
            call.respond(HttpStatusCode.OK)
        }

        get ("/isReady") {
            call.respond(HttpStatusCode.OK)
        }

    }
}