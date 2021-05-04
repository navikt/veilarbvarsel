package no.nav.fo.veilarbvarsel.brukernotifikasjon

import no.nav.fo.veilarbvarsel.brukernotifikasjon.producers.BrukernotifikasjonBeskjedProducer
import no.nav.fo.veilarbvarsel.brukernotifikasjon.producers.BrukernotifikasjonOppgaveProducer
import no.nav.fo.veilarbvarsel.kafka.utils.KafkaCallback
import org.joda.time.LocalDateTime
import java.net.URL

class BrukernotifikasjonService(
        val beskjedProducer: BrukernotifikasjonBeskjedProducer,
        val oppgaveProducer: BrukernotifikasjonOppgaveProducer
) {

    fun sendBeskjed(
            id: String,
            fodselsnummer: String,
            groupId: String,
            message: String,
            link: URL,
            sikkerhetsnivaa: Int,
            visibleUntil: LocalDateTime?,
            callback: KafkaCallback) {

        beskjedProducer.send(id, fodselsnummer, groupId, message, link, sikkerhetsnivaa, visibleUntil, callback)
    }

    fun sendOppgave(
            id: String,
            fodselsnummer: String,
            groupId: String,
            message: String,
            link: URL,
            sikkerhetsnivaa: Int,
            callback: KafkaCallback
    ) {
        oppgaveProducer.send(id, fodselsnummer, groupId, message, link, sikkerhetsnivaa, callback)
    }
}