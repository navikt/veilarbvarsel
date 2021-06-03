package no.nav.fo.veilarbvarsel.config.system

import io.ktor.application.*
import io.ktor.metrics.micrometer.*
import io.ktor.routing.*
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.binder.system.UptimeMetrics
import no.nav.fo.veilarbvarsel.config.ApplicationContext
import no.nav.fo.veilarbvarsel.varsel.varselApi


fun Application.healthModule(appContext: ApplicationContext) {
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

    routing {
        trace {
            application.log.debug(it.buildText())
        }
        healthApi(appContext.metrics)
        //varselApi(appContext.varselProducer)
        varselApi()
    }
}
