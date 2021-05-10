package no.nav.fo.veilarbvarsel.varsel

import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.fo.veilarbvarsel.brukernotifikasjonclient.BrukernotifikasjonClient
import no.nav.fo.veilarbvarsel.dabevents.CreateVarselPayload
import no.nav.fo.veilarbvarsel.dabevents.DabEventProducer
import no.nav.fo.veilarbvarsel.config.kafka.utils.KafkaCallback
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*

class VarselService(
    private val dabEventProducer: DabEventProducer,
    private val brukernotifikasjon: BrukernotifikasjonClient,
    private val metrics: PrometheusMeterRegistry
) {

    val logger: Logger = LoggerFactory.getLogger(javaClass)


    fun create(transactionId: UUID, event: CreateVarselPayload) {
        when (event.varselType) {
            VarselType.BESKJED -> sendBeskjed(transactionId, event)
            VarselType.OPPGAVE -> sendOppgave(transactionId, event)
        }
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
            event.toVarsel(),
            defaultCallback(
                transactionId,
                "Successfully sent Beskjed from system ${event.system} with id ${event.id} to Brukernotifikasjon",
                "Failed to send Beskjed from system ${event.system} with id ${event.id} to Brukernotifikasjon",
            )
        )
    }

    private fun sendOppgave(transactionId: UUID, event: CreateVarselPayload) {
        brukernotifikasjon.sendOppgave(
            event.toVarsel(),
            defaultCallback(
                transactionId,
                "Successfully sent Oppgave from system ${event.system} with id ${event.id} to Brukernotifikasjon",
                "Failed to send Oppgave from system ${event.system} with id ${event.id} to Brukernotifikasjon",
            )
        )
    }

    private fun createId(system: String, id: String): String {
        return "$system:::$id"
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

    private fun CreateVarselPayload.toVarsel(): Varsel {
        return Varsel(
            system,
            id,
            varselType,
            fodselsnummer,
            groupId,
            URL(link),
            message,
            sikkerhetsnivaa,
            visibleUntil,
            externalVarsling
        )
    }


}
