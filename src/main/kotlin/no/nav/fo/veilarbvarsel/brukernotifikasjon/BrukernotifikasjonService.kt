package no.nav.fo.veilarbvarsel.brukernotifikasjon

import no.nav.fo.veilarbvarsel.brukernotifikasjon.producers.BrukernotifikasjonBeskjedProducer
import no.nav.fo.veilarbvarsel.brukernotifikasjon.producers.BrukernotifikasjonDoneProducer
import no.nav.fo.veilarbvarsel.brukernotifikasjon.producers.BrukernotifikasjonOppgaveProducer
import no.nav.fo.veilarbvarsel.kafka.utils.KafkaCallback
import org.joda.time.LocalDateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.net.URL

class BrukernotifikasjonService(
        private val beskjedProducer: BrukernotifikasjonBeskjedProducer,
        private val oppgaveProducer: BrukernotifikasjonOppgaveProducer,
        private val doneProducer: BrukernotifikasjonDoneProducer
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

    fun sendDone(
        id: String,
        fodselsnummer: String,
        groupId: String,
        callback: KafkaCallback
    ) {
        doneProducer.send(id, fodselsnummer, groupId, callback)
    }
}