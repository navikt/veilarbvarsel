package no.nav.fo.veilarbvarsel.kafka

import no.nav.brukernotifikasjon.schemas.Beskjed
import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.fo.veilarbvarsel.kafka.domain.BeskjedRecord
import no.nav.fo.veilarbvarsel.kafka.domain.VarselCallback
import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.RecordMetadata
import org.slf4j.LoggerFactory
import java.util.*

interface BeskjedProducer {
    fun send(beskjedRecord: BeskjedRecord, callback: VarselCallback)
}

class BeskjedProducerImpl(val kafkaProducer: KafkaMessageProducer<Nokkel, Beskjed>) : BeskjedProducer {

    var logger = LoggerFactory.getLogger(this.javaClass)

    private val systemUser: String = System.getenv("SYSTEM_USER") ?: "VEILARBVARSEL"
    private val topic: String =
        System.getenv("BRUKERNOTIFIKASJON_OPPGAVE_TOPIC") ?: "aapen-brukernotifikasjon-nyBeskjed-v1-testing"

    override fun send(beskjedRecord: BeskjedRecord, callback: VarselCallback) {
        val id = UUID.randomUUID()
        val nokkel = Nokkel(systemUser, id.toString())

        kafkaProducer.send(topic, nokkel, beskjedRecord.toBeskjed(), object : Callback {
            override fun onCompletion(metadata: RecordMetadata, exception: Exception?) {
                if (exception == null) {
                    logger.info("Beskjed ordered with id ${id} for user ${beskjedRecord.personIdent}")
                    callback.onSuccess(id)
                } else {
                    logger.error("Failed to order beskjed for user ${beskjedRecord.personIdent}", exception)
                    callback.onFailure(exception)
                }
            }
        })

    }


}