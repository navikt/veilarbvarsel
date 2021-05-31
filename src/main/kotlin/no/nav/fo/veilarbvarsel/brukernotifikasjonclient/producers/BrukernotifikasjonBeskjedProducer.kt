package no.nav.fo.veilarbvarsel.brukernotifikasjonclient.producers

import no.nav.brukernotifikasjon.schemas.Beskjed
import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.brukernotifikasjon.schemas.builders.BeskjedBuilder
import no.nav.fo.veilarbvarsel.config.KafkaEnvironment
import no.nav.fo.veilarbvarsel.config.kafka.KafkaProducerWrapper
import no.nav.fo.veilarbvarsel.config.kafka.utils.KafkaCallback
import no.nav.fo.veilarbvarsel.config.kafka.utils.props.KafkaAvroProducerProperties
import no.nav.fo.veilarbvarsel.varsel.Varsel

class BrukernotifikasjonBeskjedProducer(
    env: KafkaEnvironment,
    private val systemUser: String,
    topic: String
) {

    private val producer: KafkaProducerWrapper<Nokkel, Beskjed> = KafkaProducerWrapper(
        KafkaAvroProducerProperties(env).getProperties(),
        topic
    )

    fun send(varsel: Varsel, callback: KafkaCallback?) {
        val nokkel = Nokkel(systemUser, varsel.getSystemId())
        val beskjed = createBeskjed(varsel)

        producer.send(nokkel, beskjed, callback)
    }

    private fun createBeskjed(varsel: Varsel): Beskjed {
        val builder = BeskjedBuilder()
            .withTidspunkt(java.time.LocalDateTime.now())
            .withFodselsnummer(varsel.fodselsnummer)
            .withGrupperingsId(varsel.groupId)
            .withTekst(varsel.message)
            .withLink(varsel.link)
            .withSikkerhetsnivaa(varsel.sikkerhetsnivaa)

        if (varsel.visibleUntil != null) {
            builder.withSynligFremTil(varsel.visibleUntil)
        }

        return builder.build()
    }


}
