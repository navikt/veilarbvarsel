package no.nav.fo.veilarbvarsel.config

import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.fo.veilarbvarsel.brukernotifikasjon.BrukernotifikasjonService
import no.nav.fo.veilarbvarsel.brukernotifikasjon.producers.BrukernotifikasjonBeskjedProducer
import no.nav.fo.veilarbvarsel.brukernotifikasjon.producers.BrukernotifikasjonOppgaveProducer
import no.nav.fo.veilarbvarsel.dabevents.DabEventConsumer
import no.nav.fo.veilarbvarsel.dabevents.DabEventProducer
import no.nav.fo.veilarbvarsel.dabevents.DabEventService
import no.nav.fo.veilarbvarsel.varsel.VarselService

class ApplicationContext {
    val environment = Environment()
    //val database = PostgresDatabase(environment.database)

    val metrics = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    val dabEventProducer = DabEventProducer(environment.kafka, environment.kafkaTopics.dabEvents)
    val beskjedProducer = BrukernotifikasjonBeskjedProducer(environment.kafka, environment.systemUser, environment.kafkaTopics.doknotifikasjonBeskjed)
    val oppgaveProducer = BrukernotifikasjonOppgaveProducer(environment.kafka, environment.systemUser, environment.kafkaTopics.doknotifikasjonOppgave)

    val dabEventService = DabEventService(dabEventProducer)
    val brukernotifikasjonService = BrukernotifikasjonService(beskjedProducer, oppgaveProducer)
    val varselService = VarselService(dabEventProducer, brukernotifikasjonService, metrics)

    val dabEventConsumer = DabEventConsumer(
            env = environment.kafka,
            systemUser = environment.systemUser,
            topics = listOf(environment.kafkaTopics.dabEvents),
            service = varselService
    )


}