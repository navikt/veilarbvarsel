package no.nav.fo.veilarbvarsel.brukernotifikasjon.producers

import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.brukernotifikasjon.schemas.Oppgave
import no.nav.brukernotifikasjon.schemas.builders.OppgaveBuilder
import no.nav.fo.veilarbvarsel.config.KafkaEnvironment
import no.nav.fo.veilarbvarsel.kafka.KafkaProducerWrapper
import no.nav.fo.veilarbvarsel.kafka.utils.KafkaCallback
import no.nav.fo.veilarbvarsel.kafka.utils.props.KafkaAvroProducerProperties
import java.net.URL
import java.time.LocalDateTime

class BrukernotifikasjonOppgaveProducer(
        env: KafkaEnvironment,
        private val systemUser: String,
        topic: String
) {

    private val producer: KafkaProducerWrapper<Nokkel, Oppgave> = KafkaProducerWrapper(
            KafkaAvroProducerProperties(env).getProperties(),
            topic
    )

    fun send(
            varselId: String,
            fodselsnummer: String,
            groupId: String,
            message: String,
            link: URL,
            sikkerhetsnivaa: Int,
            callback: KafkaCallback?
    ) {
        val nokkel = Nokkel(systemUser, varselId)

        val oppgave = OppgaveBuilder()
                .withTidspunkt(LocalDateTime.now())
                .withFodselsnummer(fodselsnummer)
                .withGrupperingsId(groupId)
                .withTekst(message)
                .withLink(link)
                .withSikkerhetsnivaa(sikkerhetsnivaa)
                .build()

        producer.send(nokkel, oppgave, callback)
    }
}