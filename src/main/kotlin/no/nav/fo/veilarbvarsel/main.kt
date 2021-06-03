package no.nav.fo.veilarbvarsel

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.fo.veilarbvarsel.config.ApplicationContext
import no.nav.fo.veilarbvarsel.config.system.healthModule

fun main() {
    val port = System.getenv("SERVER_PORT")?.toInt() ?: 8080
    val server = embeddedServer(Netty, port, module = Application::mainModule)
    server.start()
}

fun Application.mainModule(appContext: ApplicationContext = ApplicationContext()) {
    healthModule(appContext)

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

//    install(BackgroundJob.BackgroundJobFeature("Events Consumer")) {
//        job = appContext.eventConsumer
//    }

    appContext.eventConsumer.run()

    while (true) {
        Thread.sleep(1000)
        println("Heartbeat")
    }

}
