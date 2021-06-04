package no.nav.fo.veilarbvarsel

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.fo.veilarbvarsel.config.ApplicationContext
import no.nav.fo.veilarbvarsel.config.system.features.BackgroundJob
import no.nav.fo.veilarbvarsel.config.system.healthModule
import org.slf4j.LoggerFactory

fun main() {
    val logger = LoggerFactory.getLogger("Main")

    val port = System.getenv("SERVER_PORT")?.toInt() ?: 8080
    val server = embeddedServer(Netty, port, module = Application::mainModule)
    server.start()

    logger.info("I be ded")
}

fun Application.mainModule(appContext: ApplicationContext = ApplicationContext()) {
    val logger = LoggerFactory.getLogger(javaClass)

    healthModule(appContext)

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(BackgroundJob.BackgroundJobFeature("Events Consumer")) {
        job = appContext.eventConsumer
    }

//    appContext.eventConsumer.run()
}
