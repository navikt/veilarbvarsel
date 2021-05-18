package no.nav.fo.veilarbvarsel.config

import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.fo.veilarbvarsel.brukernotifikasjonclient.BrukernotifikasjonClient
import no.nav.fo.veilarbvarsel.brukernotifikasjonclient.producers.BrukernotifikasjonBeskjedProducer
import no.nav.fo.veilarbvarsel.brukernotifikasjonclient.producers.BrukernotifikasjonDoneProducer
import no.nav.fo.veilarbvarsel.brukernotifikasjonclient.producers.BrukernotifikasjonOppgaveProducer
import no.nav.fo.veilarbvarsel.varsel.VarselEventConsumer
import no.nav.fo.veilarbvarsel.varsel.VarselEventProducer
import no.nav.fo.veilarbvarsel.varsel.VarselService

class ApplicationContext {
    val environment = Environment()

    val metrics = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    val eventProducer = VarselEventProducer(
        env = environment.kafka,
        topic = environment.kafkaTopics.varselKvitteringOutgoing
    )
//    val beskjedProducer = BrukernotifikasjonBeskjedProducer(
//        env = environment.kafka,
//        systemUser = environment.systemUser,
//        topic = environment.kafkaTopics.doknotifikasjonBeskjed
//    )
//    val oppgaveProducer = BrukernotifikasjonOppgaveProducer(
//        env = environment.kafka,
//        systemUser = environment.systemUser,
//        topic = environment.kafkaTopics.doknotifikasjonOppgave
//    )
//    val doneProducer = BrukernotifikasjonDoneProducer(
//        env = environment.kafka,
//        systemUser = environment.systemUser,
//        topic = environment.kafkaTopics.doknotifikasjonDone
//    )

//    val brukernotifikasjonClient = BrukernotifikasjonClient(beskjedProducer, oppgaveProducer, doneProducer)
    val brukernotifikasjonClient = BrukernotifikasjonClient()
    val varselService = VarselService(brukernotifikasjonClient)

    val eventConsumer = VarselEventConsumer(
        env = environment.kafka,
        systemUser = environment.systemUser,
        topics = listOf(environment.kafkaTopics.varselIncoming),
        service = varselService
    )
}
