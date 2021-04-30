package no.nav.fo.veilarbvarsel

import io.ktor.application.*
import io.ktor.metrics.micrometer.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.fo.veilarbvarsel.domain.VarselType
import no.nav.fo.veilarbvarsel.kafka.internal.InternalEventProducer
import no.nav.fo.veilarbvarsel.system.systemRouter
import org.slf4j.LoggerFactory
import java.util.*

val logger = LoggerFactory.getLogger("Main")

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    val server = embeddedServer(Netty, port, module = Application::server)
    server.start()
}

fun Application.server() {
    val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    install(MicrometerMetrics) {
        registry = appMicrometerRegistry

        meterBinders = listOf(
            JvmMemoryMetrics(),
            JvmGcMetrics(),
            ProcessorMetrics()
        )
    }

/*    DB.connect()
    DB.setupSchemas()

    val service = VarselServiceImpl()

    install(BackgroundJob.BackgroundJobFeature("Kafka Internal Consumer")) {
        job = KafkaInternalConsumer(service)
    }

    install(BackgroundJob.BackgroundJobFeature("Varsel Sender")) {
        job = VarselSender(service)
    }*/

    routing {
        trace {
            application.log.debug(it.buildText())
        }
        get("/metrics") {
            call.respond(appMicrometerRegistry.scrape())
        }
        systemRouter()
    }

/*
    BrukerNotifikasjonBeskjedProducer.send(
        UUID.randomUUID().toString(),
        "10108003980",
        "Group_1",
        "Dette er en test",
        URL("https://www.nav.no"),
        4,
        LocalDateTime.now().plusHours(1),
        object : KafkaCallback {
            override fun onSuccess() {
                logger.info("[BRUKERNOTIFIKASJON] Sent message")
            }

            override fun onFailure(exception: Exception) {
                logger.info("[BRUKERNOTIFIKASJON] Failed to send message", exception)
            }

        }
    )
*/

//    while (true) {
//        Thread.sleep(1000)
//        sendVarsel()
//    }

}

fun sendVarsel() {
    val transactionId = UUID.randomUUID()
    val id = UUID.randomUUID().toString()

    InternalEventProducer.createVarsel(
        transactionId,
        id,
        VarselType.MELDING,
        "12345678910",
        "groupx",
        "Dette er en melding",
        4,
        null,
        null)

}