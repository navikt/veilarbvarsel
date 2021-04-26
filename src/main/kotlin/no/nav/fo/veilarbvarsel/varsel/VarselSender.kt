package no.nav.fo.veilarbvarsel.varsel

import no.nav.fo.veilarbvarsel.features.ClosableJob
import no.nav.fo.veilarbvarsel.kafka.KafkaProducer
import org.joda.time.LocalDateTime
import org.slf4j.LoggerFactory

class VarselSender(val service: VarselService) : ClosableJob {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val varselOutgoingTopic: String = System.getenv("TOPIC_VARSEL_OUTGOING") ?: "varsel_outgoing"

    var shutdown = false
    var running = false

    val producer = KafkaProducer<String, String>()

    override fun run() {
        logger.info("Starting Varsel Sender")
        running = true

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                close()
            }
        })

        while (!shutdown) {
            val varsler = service.getAllNotSendingOrCanceled(LocalDateTime.now().minusMinutes(1))

            varsler.forEach { varsel ->

                //FIXME Send to Varsel
                service.sending(varsel.varselId)
                service.sent(varsel.varselId)
            }


            Thread.sleep(5000)
        }

        running = false
    }

    override fun close() {
        logger.info("Closing Varsel Sender...")

        shutdown = true

        while (running) {
            Thread.sleep(100)
        }

        logger.info("Varsel Sender Closed!")
    }
}