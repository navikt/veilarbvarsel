package no.nav.fo.veilarbvarsel.brukernotifikasjon.producers

import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.brukernotifikasjon.schemas.Oppgave
import no.nav.brukernotifikasjon.schemas.builders.OppgaveBuilder
import no.nav.fo.veilarbvarsel.config.KafkaEnvironment
import no.nav.fo.veilarbvarsel.kafka.KafkaProducerWrapper
import no.nav.fo.veilarbvarsel.kafka.utils.KafkaCallback
import no.nav.fo.veilarbvarsel.kafka.utils.props.KafkaAvroProducerProperties
import no.nav.fo.veilarbvarsel.varsel.domain.Varsel
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
        varsel: Varsel,
        callback: KafkaCallback?
    ) {
        val nokkel = Nokkel(systemUser, varsel.getSystemId())

        val oppgave = OppgaveBuilder()
            .withTidspunkt(LocalDateTime.now())
            .withFodselsnummer(varsel.fodselsnummer)
            .withGrupperingsId(varsel.groupId)
            .withTekst(varsel.message)
            .withLink(varsel.link)
            .withSikkerhetsnivaa(varsel.sikkerhetsnivaa)
            .build()

        producer.send(nokkel, oppgave, callback)
    }
}