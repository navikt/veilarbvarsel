package no.nav.fo.veilarbvarsel.brukernotifikasjonclient

import no.nav.fo.veilarbvarsel.brukernotifikasjonclient.producers.BrukernotifikasjonBeskjedProducer
import no.nav.fo.veilarbvarsel.brukernotifikasjonclient.producers.BrukernotifikasjonDoneProducer
import no.nav.fo.veilarbvarsel.brukernotifikasjonclient.producers.BrukernotifikasjonOppgaveProducer
import no.nav.fo.veilarbvarsel.config.kafka.utils.KafkaCallback
import no.nav.fo.veilarbvarsel.varsel.Varsel
import org.slf4j.LoggerFactory

class BrukernotifikasjonClient(
    private val beskjedProducer: BrukernotifikasjonBeskjedProducer,
    private val oppgaveProducer: BrukernotifikasjonOppgaveProducer,
    private val doneProducer: BrukernotifikasjonDoneProducer
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun sendBeskjed(
        varsel: Varsel,
        callback: KafkaCallback
    ) {
        logger.info("Sender beskjed til Brukernotifikasjon.")
        beskjedProducer.send(varsel, callback)
    }

    fun sendOppgave(
        varsel: Varsel,
        callback: KafkaCallback
    ) {
        logger.info("Sender oppgave til Brukernotifikasjon.")
        oppgaveProducer.send(varsel, callback)
    }

    fun sendDone(
        id: String,
        fodselsnummer: String,
        groupId: String,
        callback: KafkaCallback
    ) {
        logger.info("Sender done til Brukernotifikasjon.")
        doneProducer.send(id, fodselsnummer, groupId, callback)
    }
}
