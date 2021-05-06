package no.nav.fo.veilarbvarsel.brukernotifikasjon

import no.nav.fo.veilarbvarsel.brukernotifikasjon.producers.BrukernotifikasjonBeskjedProducer
import no.nav.fo.veilarbvarsel.brukernotifikasjon.producers.BrukernotifikasjonDoneProducer
import no.nav.fo.veilarbvarsel.brukernotifikasjon.producers.BrukernotifikasjonOppgaveProducer
import no.nav.fo.veilarbvarsel.kafka.utils.KafkaCallback
import no.nav.fo.veilarbvarsel.varsel.domain.Varsel

class BrukernotifikasjonService(
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