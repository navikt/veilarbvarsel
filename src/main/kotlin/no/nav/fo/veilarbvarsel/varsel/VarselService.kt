package no.nav.fo.veilarbvarsel.varsel

import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.fo.veilarbvarsel.brukernotifikasjon.BrukernotifikasjonService
import no.nav.fo.veilarbvarsel.dabevents.*
import no.nav.fo.veilarbvarsel.kafka.utils.KafkaCallback
import no.nav.fo.veilarbvarsel.varsel.domain.VarselType
import org.joda.time.LocalDateTime
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.net.URL
import java.util.*

class VarselService(
        private val dabEventProducer: DabEventProducer,
        private val brukernotifikasjon: BrukernotifikasjonService,
        private val metrics: PrometheusMeterRegistry
) {

    fun create(transactionId: UUID, event: CreateVarselPayload) {
        when (event.varselType) {
            VarselType.BESKJED -> sendBeskjed(transactionId, event)
            VarselType.OPPGAVE -> sendOppgave(transactionId, event)
        }
    }

    fun created(transactionId: UUID, system: String, id: String) {
        dabEventProducer.send(DabEvent(
                transactionId,
                LocalDateTime.now(),
                "VARSEL",
                EventType.CREATED,
                VarselCreatedPayload(
                        system,
                        id
                )
        ))
    }

    private fun sendBeskjed(transactionId: UUID, event: CreateVarselPayload) {
        brukernotifikasjon.sendBeskjed(
                "${event.system}:::${event.id}",
                event.fodselsnummer,
                event.groupId,
                event.message,
                URL(event.link),
                event.sikkerhetsnivaa,
                event.visibleUntil,
                object : KafkaCallback {
                    override fun onSuccess() {
           //             metrics.get("BESKJED_SENT").counter().increment()
                        created(transactionId, event.system, event.id)
                    }
                    override fun onFailure(exception: Exception) {
         //               metrics.get("BESKJED_SENDING_FAILED").counter().increment()
                        super.onFailure(exception)
                    }
                }
        )
    }

    private fun sendOppgave(transactionId: UUID, event: CreateVarselPayload) {
        brukernotifikasjon.sendOppgave(
                "${event.system}:::${event.id}",
                event.fodselsnummer,
                event.groupId,
                event.message,
                URL(event.link),
                event.sikkerhetsnivaa,
                object : KafkaCallback {
                    override fun onSuccess() {
             //           metrics.get("OPPGAVE_SENT").counter().increment()
                        created(transactionId, event.system, event.id)
                    }
                    override fun onFailure(exception: Exception) {
               //         metrics.get("OPPGAVE_SENDING_FAILED").counter().increment()
                        super.onFailure(exception)
                    }
                }

        )
    }


}