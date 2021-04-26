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

    install(BackgroundJob.BackgroundJobFeature) {
        name = "Kafka Internal Consumer"
        job = setupInternalConsumer(service)
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

fun setupInternalConsumer(service: VarselService): KafkaInternalConsumer {
    val topic = System.getenv("KAFKA_INTERNAL_TOPIC")?: "TEST_INTERNAL_TOPIC"

    return object : KafkaInternalConsumer(topic) {

        override fun handle(data: InternalEvent) {
            when (data.payload) {
                is CreateVarselPayload -> service.add(
                    data.payload.varselId,
                    data.payload.varselType,
                    data.payload.fodselsnummer,
                    data.payload.groupId,
                    data.payload.message,
                    data.payload.sikkerhetsnivaa,
                    data.payload.visibleUntil
                )
                else -> TODO("Not yet implemented")
            }
        }
    }
}
