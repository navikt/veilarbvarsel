package no.nav.fo.veilarbvarsel.config

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.metrics.micrometer.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.binder.system.UptimeMetrics
import no.nav.fo.veilarbvarsel.system.features.BackgroundJob
import no.nav.fo.veilarbvarsel.system.healthApi
import no.nav.fo.veilarbvarsel.varsel.varselApi

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    val server = embeddedServer(Netty, port, module = Application::mainModule)
    server.start()
}

fun Application.mainModule(appContext: ApplicationContext = ApplicationContext()) {
    install(MicrometerMetrics) {
        registry = appContext.metrics

        meterBinders = listOf(
                ClassLoaderMetrics(),
                JvmMemoryMetrics(),
                JvmGcMetrics(),
                ProcessorMetrics(),
                JvmThreadMetrics(),
                FileDescriptorMetrics(),
                UptimeMetrics(),
        )
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(BackgroundJob.BackgroundJobFeature("DAB Events Consumer")) {
        job = appContext.dabEventConsumer
    }

    routing {
        trace {
            application.log.debug(it.buildText())
        }
        healthApi(appContext.metrics)
        varselApi(appContext.dabEventService)
    }

    configureShutdownHook(appContext)
}

private fun Application.configureShutdownHook(appContext: ApplicationContext) {
    environment.monitor.subscribe(ApplicationStopPreparing) {
        appContext.dabEventConsumer.close()
        appContext.dabEventProducer.close()
    }
}
