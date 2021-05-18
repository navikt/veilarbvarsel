package no.nav.fo.veilarbvarsel.brukernotifikasjonclient

import no.nav.fo.veilarbvarsel.config.kafka.utils.KafkaCallback
import no.nav.fo.veilarbvarsel.varsel.Varsel
import org.slf4j.LoggerFactory

class BrukernotifikasjonClient() {

    val logger = LoggerFactory.getLogger(javaClass)

    fun sendBeskjed(
        varsel: Varsel,
        callback: KafkaCallback
    ) {
        logger.info("Sending Beskjed: $varsel")
//        beskjedProducer.send(varsel, callback)
    }

    fun sendOppgave(
        varsel: Varsel,
        callback: KafkaCallback
    ) {
        logger.info("Sending Oppgave: $varsel")
//        oppgaveProducer.send(varsel, callback)
    }

    fun sendDone(
        id: String,
        fodselsnummer: String,
        groupId: String,
        callback: KafkaCallback
    ) {
        logger.info("Sending Done: $id, $fodselsnummer, $groupId")
//        doneProducer.send(id, fodselsnummer, groupId, callback)
    }
}
