package no.nav.fo.veilarbvarsel.config

import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.fo.veilarbvarsel.brukernotifikasjonclient.BrukernotifikasjonClient
import no.nav.fo.veilarbvarsel.brukernotifikasjonclient.producers.BrukernotifikasjonBeskjedProducer
import no.nav.fo.veilarbvarsel.brukernotifikasjonclient.producers.BrukernotifikasjonDoneProducer
import no.nav.fo.veilarbvarsel.brukernotifikasjonclient.producers.BrukernotifikasjonOppgaveProducer
import no.nav.fo.veilarbvarsel.events.EventConsumer
import no.nav.fo.veilarbvarsel.events.EventProducer
import no.nav.fo.veilarbvarsel.events.skal_slettes.EventService
import no.nav.fo.veilarbvarsel.varsel.VarselService

class ApplicationContext {
    val environment = Environment()

    val metrics = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    val eventProducer = EventProducer(
        env = environment.kafka,
        topic = environment.kafkaTopics.dabEvents
    )
    val beskjedProducer = BrukernotifikasjonBeskjedProducer(
        env = environment.kafka,
        systemUser = environment.systemUser,
        topic = environment.kafkaTopics.doknotifikasjonBeskjed
    )
    val oppgaveProducer = BrukernotifikasjonOppgaveProducer(
        env = environment.kafka,
        systemUser = environment.systemUser,
        topic = environment.kafkaTopics.doknotifikasjonOppgave
    )
    val doneProducer = BrukernotifikasjonDoneProducer(
        env = environment.kafka,
        systemUser = environment.systemUser,
        topic = environment.kafkaTopics.doknotifikasjonDone
    )

    val dabEventService = EventService(eventProducer)
    val brukernotifikasjonService = BrukernotifikasjonClient(beskjedProducer, oppgaveProducer, doneProducer)
    val varselService = VarselService(eventProducer, brukernotifikasjonService, metrics)

    val eventConsumer = EventConsumer(
        env = environment.kafka,
        systemUser = environment.systemUser,
        topics = listOf(environment.kafkaTopics.dabEvents),
        service = varselService
    )
}
