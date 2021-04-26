package no.nav.fo.veilarbvarsel

import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.fo.veilarbvarsel.varsel.VarselServiceImpl
import no.nav.fo.veilarbvarsel.domain.VarselType
import no.nav.fo.veilarbvarsel.domain.events.CreateVarselPayload
import no.nav.fo.veilarbvarsel.domain.events.InternalEvent
import no.nav.fo.veilarbvarsel.features.BackgroundJob
import no.nav.fo.veilarbvarsel.kafka.KafkaInternalConsumer
import no.nav.fo.veilarbvarsel.test.InternalProducer
import no.nav.fo.veilarbvarsel.varsel.VarselSender
import no.nav.fo.veilarbvarsel.varsel.VarselService
import java.util.*

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    val server = embeddedServer(Netty, port, module = Application::server)
    server.start()
}

fun Application.server() {
    DB.connect()
    DB.setupSchemas()

    val service = VarselServiceImpl()

    install(BackgroundJob.BackgroundJobFeature("Kafka Internal Consumer")) {
        job = KafkaInternalConsumer(service)
    }

    install(BackgroundJob.BackgroundJobFeature("Varsel Sender")) {
        job = VarselSender(service)
    }

    InternalProducer.createVarsel(
        UUID.randomUUID().toString(),
        VarselType.MELDING,
        "12345678910",
        "groupx",
        "Dette er en melding",
        4,
        null
    )
}