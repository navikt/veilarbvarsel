package no.nav.fo.veilarbvarsel.varsel

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.nav.fo.veilarbvarsel.kafka.KafkaProducer
import org.joda.time.LocalDateTime
import org.slf4j.LoggerFactory

class VarselSender(val service: VarselService) {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val varselOutgoingTopic: String = System.getenv("TOPIC_VARSEL_OUTGOING")?: "varsel_outgoing"

    var running = false
    lateinit var job: Job

    val producer = KafkaProducer<String, String>()

    fun start() {
        running = true

        job = GlobalScope.launch {
            logger.info("Varsel Sender started.")

            while(running) {
                logger.info("Sending varsler!")
                val varsler = service.getAllNotSendingOrCanceled(LocalDateTime.now().minusMinutes(2))

                varsler.forEach { varsel ->

                    //FIXME Send to Varsel
                    service.sending(varsel.varselId)
                    logger.info("   Sending varsel with id ${varsel.varselId}")
                    service.sent(varsel.varselId)
                    logger.info("   Sent varsel with id ${varsel.varselId}")
                }


                delay(60000)
            }

            logger.info("Varsel Sender stopped.")
        }

    }

}