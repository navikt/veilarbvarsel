package no.nav.fo.veilarbvarsel.kafka

import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.brukernotifikasjon.schemas.Oppgave
import no.nav.fo.veilarbvarsel.kafka.domain.OppgaveRecord
import no.nav.fo.veilarbvarsel.kafka.domain.VarselCallback
import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.slf4j.LoggerFactory
import java.util.*

interface OppgaveProducer {
    fun send(oppgaveRecord: OppgaveRecord, callback: VarselCallback)
}

class OppgaveProducerImpl(val kafkaProducer: KafkaProducer<Nokkel, Oppgave>): OppgaveProducer {

    val logger = LoggerFactory.getLogger(this.javaClass)

    private val systemUser: String = System.getenv("SYSTEM_USER") ?: "VEILARBVARSEL"
    private val topic: String = System.getenv("BRUKERNOTIFIKASJON_OPPGAVE_TOPIC") ?: "aapen-brukernotifikasjon-nyOppgave-v1-testing"

    override fun send(oppgaveRecord: OppgaveRecord, callback: VarselCallback) {
        val id = UUID.randomUUID()
        val nokkel = Nokkel(systemUser, id.toString())

        val record = ProducerRecord(topic, nokkel, oppgaveRecord.toOppgave())

        kafkaProducer.send(record, object : Callback {
            override fun onCompletion(metadata: RecordMetadata, exception: Exception?) {
                if (exception == null) {
                    logger.info("Oppgave ordered with id ${id} for user ${oppgaveRecord.personIdent}")
                    callback.onSuccess(id)
                } else {
                    logger.error("Failed to order oppgave for user ${oppgaveRecord.personIdent}", exception)
                    callback.onFailure(exception)
                }
            }
        })
    }
}
