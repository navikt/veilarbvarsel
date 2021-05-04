package no.nav.fo.veilarbvarsel.brukernotifikasjon.producers

import no.nav.brukernotifikasjon.schemas.Beskjed
import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.brukernotifikasjon.schemas.builders.BeskjedBuilder
import no.nav.fo.veilarbvarsel.config.KafkaEnvironment
import no.nav.fo.veilarbvarsel.kafka.KafkaProducerWrapper
import no.nav.fo.veilarbvarsel.kafka.utils.KafkaCallback
import no.nav.fo.veilarbvarsel.kafka.utils.props.KafkaAvroProducerProperties
import org.joda.time.LocalDateTime
import java.net.URL

class BrukernotifikasjonBeskjedProducer(
        env: KafkaEnvironment,
        private val systemUser: String,
        topic: String
) {

    private val producer: KafkaProducerWrapper<Nokkel, Beskjed> = KafkaProducerWrapper(
            KafkaAvroProducerProperties(env).getProperties(),
            topic
    )

    fun send(
            id: String,
            fodselsnummer: String,
            groupId: String,
            message: String,
            link: URL,
            sikkerhetsnivaa: Int,
            visibleUntil: LocalDateTime?,
            callback: KafkaCallback?
    ) {
        val nokkel = Nokkel(systemUser, id)
        val beskjed = createBeskjed(id, fodselsnummer, groupId, message, link, sikkerhetsnivaa, visibleUntil)

        producer.send(nokkel, beskjed, callback)
    }

    private fun createBeskjed(
            varselId: String,
            fodselsnummer: String,
            groupId: String,
            message: String,
            link: URL,
            sikkerhetsnivaa: Int,
            visibleUntil: LocalDateTime?,
    ): Beskjed {
        val builder = BeskjedBuilder()
                .withTidspunkt(java.time.LocalDateTime.now())
                .withFodselsnummer(fodselsnummer)
                .withGrupperingsId(groupId)
                .withTekst(message)
                .withLink(link)
                .withSikkerhetsnivaa(sikkerhetsnivaa)

        if (visibleUntil != null) {
            builder.withSynligFremTil(
                    java.time.LocalDateTime.of(
                            visibleUntil.year,
                            visibleUntil.monthOfYear,
                            visibleUntil.dayOfMonth,
                            visibleUntil.hourOfDay,
                            visibleUntil.minuteOfHour,
                            visibleUntil.secondOfMinute
                    )
            )
        }

        return builder.build()
    }


}