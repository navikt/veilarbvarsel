package no.nav.fo.veilarbvarsel.brukernotifikasjon.producers

import no.nav.brukernotifikasjon.schemas.Done
import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.brukernotifikasjon.schemas.builders.DoneBuilder
import no.nav.fo.veilarbvarsel.config.KafkaEnvironment
import no.nav.fo.veilarbvarsel.kafka.KafkaProducerWrapper
import no.nav.fo.veilarbvarsel.kafka.utils.KafkaCallback
import no.nav.fo.veilarbvarsel.kafka.utils.props.KafkaAvroProducerProperties
import java.time.LocalDateTime

class BrukernotifikasjonDoneProducer(
    env: KafkaEnvironment,
    private val systemUser: String,
    topic: String
) {

    private val producer: KafkaProducerWrapper<Nokkel, Done> = KafkaProducerWrapper(
        KafkaAvroProducerProperties(env).getProperties(),
        topic
    )

    fun send(
        varselId: String,
        fodselsnummer: String,
        groupId: String,
        callback: KafkaCallback
    ) {
        val nokkel = Nokkel(systemUser, varselId)

        val event = DoneBuilder()
            .withFodselsnummer(fodselsnummer)
            .withGrupperingsId(groupId)
            .withTidspunkt(LocalDateTime.now())
            .build()

        producer.send(nokkel, event, callback)
    }

}