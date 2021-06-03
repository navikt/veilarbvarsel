package no.nav.fo.veilarbvarsel.brukernotifikasjonclient

import no.nav.fo.veilarbvarsel.config.kafka.utils.KafkaCallback
import no.nav.fo.veilarbvarsel.varsel.Varsel

class BrukernotifikasjonClient(
//    private val beskjedProducer: BrukernotifikasjonBeskjedProducer,
//    private val oppgaveProducer: BrukernotifikasjonOppgaveProducer,
//    private val doneProducer: BrukernotifikasjonDoneProducer
) {

    fun sendBeskjed(
        varsel: Varsel,
        callback: KafkaCallback
    ) {
        println("HERE1")
        //beskjedProducer.send(varsel, callback)
    }

    fun sendOppgave(
        varsel: Varsel,
        callback: KafkaCallback
    ) {
        println("HERE2")
        //oppgaveProducer.send(varsel, callback)
    }

    fun sendDone(
        id: String,
        fodselsnummer: String,
        groupId: String,
        callback: KafkaCallback
    ) {
        println("HERE3")
        //doneProducer.send(id, fodselsnummer, groupId, callback)
    }
}
