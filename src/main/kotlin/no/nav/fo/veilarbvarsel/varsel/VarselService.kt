package no.nav.fo.veilarbvarsel.varsel

import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.fo.veilarbvarsel.brukernotifikasjon.BrukernotifikasjonService
import no.nav.fo.veilarbvarsel.dabevents.*
import no.nav.fo.veilarbvarsel.kafka.utils.KafkaCallback
import no.nav.fo.veilarbvarsel.varsel.domain.VarselType
import org.joda.time.LocalDateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*

class VarselService(
    private val dabEventProducer: DabEventProducer,
    private val brukernotifikasjon: BrukernotifikasjonService,
    private val metrics: PrometheusMeterRegistry
) {

    val logger: Logger = LoggerFactory.getLogger(javaClass)


    fun create(transactionId: UUID, event: CreateVarselPayload) {
        when (event.varselType) {
            VarselType.BESKJED -> sendBeskjed(transactionId, event)
            VarselType.OPPGAVE -> sendOppgave(transactionId, event)
        }
    }

    fun created(transactionId: UUID, system: String, id: String) {
        dabEventProducer.send(
            DabEvent(
                transactionId,
                LocalDateTime.now(),
                "VARSEL",
                EventType.CREATED,
                VarselCreatedPayload(
                    system,
                    id
                )
            )
        )
    }

    fun done(transactionId: UUID, system: String, id: String, fodselsnummer: String, groupId: String) {
        brukernotifikasjon.sendDone(
            createId(system, id),
            fodselsnummer,
            groupId,
            defaultCallback(
                transactionId,
                "Successfully sent Done from system ${system} with id ${id} to Brukernotifikasjon",
                "Failed to send Done from system ${system} with id ${id} to Brukernotifikasjon",
            )
        )
    }

    private fun sendBeskjed(transactionId: UUID, event: CreateVarselPayload) {
        brukernotifikasjon.sendBeskjed(
            createId(event.system, event.id),
            event.fodselsnummer,
            event.groupId,
            event.message,
            URL(event.link),
            event.sikkerhetsnivaa,
            event.visibleUntil,
            defaultCallback(
                transactionId,
                "Successfully sent Beskjed from system ${event.system} with id ${event.id} to Brukernotifikasjon",
                "Failed to send Beskjed from system ${event.system} with id ${event.id} to Brukernotifikasjon",
            )
        )
    }

    private fun sendOppgave(transactionId: UUID, event: CreateVarselPayload) {
        brukernotifikasjon.sendOppgave(
            createId(event.system, event.id),
            event.fodselsnummer,
            event.groupId,
            event.message,
            URL(event.link),
            event.sikkerhetsnivaa,
            defaultCallback(
                transactionId,
                "Successfully sent Oppgave from system ${event.system} with id ${event.id} to Brukernotifikasjon",
                "Failed to send Oppgave from system ${event.system} with id ${event.id} to Brukernotifikasjon",
            )
        )
    }

    private fun createId(system: String, id: String): String {
        return "$system:::$id";
    }

    private fun defaultCallback(transactionId: UUID, successString: String, exceptionString: String): KafkaCallback {
        return object : KafkaCallback {
            override fun onSuccess() {
                logger.info("[Transaction: $transactionId]: $successString")
            }

            override fun onFailure(exception: Exception) {
                logger.error("[Transaction: $transactionId]: $exceptionString", exception)
            }

        }
    }


}