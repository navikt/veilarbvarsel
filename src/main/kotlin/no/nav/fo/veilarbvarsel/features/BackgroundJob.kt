package no.nav.fo.veilarbvarsel.features

import io.ktor.application.*
import io.ktor.util.*
import org.slf4j.LoggerFactory
import java.io.Closeable
import kotlin.concurrent.thread

class BackgroundJob(val name: String, configuration: JobConfiguration): Closeable {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val job = configuration.job

    class JobConfiguration {
        var job: ClosableJob? = null
    }

    class BackgroundJobFeature(val name: String): ApplicationFeature<Application, JobConfiguration, BackgroundJob> {
        override val key: AttributeKey<BackgroundJob> = AttributeKey("BackgroundJob-$name")

        override fun install(pipeline: Application, configure: JobConfiguration.() -> Unit): BackgroundJob {
            val configuration = JobConfiguration().apply(configure)
            val backgroundJob = BackgroundJob(name, configuration)

            configuration.job?.let { thread(name = name) { it.run() } }

            return backgroundJob
        }
    }

    override fun close() {
        logger.info("Closing $name job")
        job?.close()
        logger.info("Job $name closed")
    }

}