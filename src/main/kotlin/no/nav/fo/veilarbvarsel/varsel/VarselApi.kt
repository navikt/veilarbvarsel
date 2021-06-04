package no.nav.fo.veilarbvarsel.varsel

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.*

fun Route.varselApi(
    varselEventProducer: VarselEventProducer
) {

    val logger = LoggerFactory.getLogger(javaClass)

    route("/varsel") {

        post {
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

            call.respond(HttpStatusCode.Created)

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

            call.respond(HttpStatusCode.Created)
        }
    }
}
