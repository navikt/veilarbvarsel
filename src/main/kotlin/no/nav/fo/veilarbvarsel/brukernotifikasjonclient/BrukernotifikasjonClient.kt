package no.nav.fo.veilarbvarsel.brukernotifikasjonclient

import no.nav.fo.veilarbvarsel.brukernotifikasjonclient.producers.BrukernotifikasjonBeskjedProducer
import no.nav.fo.veilarbvarsel.brukernotifikasjonclient.producers.BrukernotifikasjonDoneProducer
import no.nav.fo.veilarbvarsel.brukernotifikasjonclient.producers.BrukernotifikasjonOppgaveProducer
import no.nav.fo.veilarbvarsel.config.kafka.utils.KafkaCallback
import no.nav.fo.veilarbvarsel.varsel.Varsel

class BrukernotifikasjonClient(
    private val beskjedProducer: BrukernotifikasjonBeskjedProducer,
    private val oppgaveProducer: BrukernotifikasjonOppgaveProducer,
    private val doneProducer: BrukernotifikasjonDoneProducer
) {

    fun sendBeskjed(
        varsel: Varsel,
        callback: KafkaCallback
    ) {
        beskjedProducer.send(varsel, callback)
    }

    fun sendOppgave(
        varsel: Varsel,
        callback: KafkaCallback
    ) {
        oppgaveProducer.send(varsel, callback)
    }

    fun sendDone(
        id: String,
        fodselsnummer: String,
        groupId: String,
        callback: KafkaCallback
    ) {
        doneProducer.send(id, fodselsnummer, groupId, callback)
    }
}
