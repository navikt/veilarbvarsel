package no.nav.fo.veilarbvarsel.brukernotifikasjon

import no.nav.brukernotifikasjon.schemas.Beskjed
import no.nav.brukernotifikasjon.schemas.Done
import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.brukernotifikasjon.schemas.Oppgave
import no.nav.brukernotifikasjon.schemas.builders.BeskjedBuilder
import no.nav.brukernotifikasjon.schemas.builders.DoneBuilder
import no.nav.brukernotifikasjon.schemas.builders.OppgaveBuilder
import no.nav.brukernotifikasjon.schemas.builders.domain.PreferertKanal
import no.nav.fo.veilarbvarsel.core.domain.Varsel
import no.nav.fo.veilarbvarsel.core.domain.VarselType
import no.nav.fo.veilarbvarsel.core.ports.BrukernotifikasjonService
import no.nav.fo.veilarbvarsel.core.utils.Callback
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BrukernotifikasjonServiceImpl(
    private val beskjedProducer: KafkaTemplate<Nokkel, Beskjed>,
    private val oppgaveProducer: KafkaTemplate<Nokkel, Oppgave>,
    private val doneProducer: KafkaTemplate<Nokkel, Done>
) : BrukernotifikasjonService {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Value("\${application.systemUser}")
    private lateinit var systemUser: String

    @Value("\${topics.doknotifikasjon.beskjed}")
    private lateinit var beskjedTopic: String

    @Value("\${topics.doknotifikasjon.oppgave}")
    private lateinit var oppgaveTopic: String

    @Value("\${topics.doknotifikasjon.done}")
    private lateinit var doneTopic: String

    override fun sendVarsel(varsel: Varsel, callback: Callback) {
        when (varsel.type) {
            VarselType.BESKJED -> sendBeskjed(varsel, callback)
            VarselType.OPPGAVE -> sendOppgave(varsel, callback)
        }
    }

    override fun sendDone(id: String, fodselsnummer: String, groupId: String, callback: Callback) {
        val nokkel = Nokkel(systemUser, id)

        val event = DoneBuilder()
            .withFodselsnummer(fodselsnummer)
            .withGrupperingsId(groupId)
            .withTidspunkt(LocalDateTime.now())
            .build()

        doneProducer.send(doneTopic, nokkel, event).addCallback(
            {
                logger.info("Sent Done on topic $doneTopic")
                callback.onSuccess()
            },
            { callback.onFailure(it) }
        )

    }


    private fun sendBeskjed(varsel: Varsel, callback: Callback) {
        val nokkel = Nokkel(systemUser, varsel.getSystemId())

        val beskjedBuilder = BeskjedBuilder()
            .withTidspunkt(LocalDateTime.now())
            .withFodselsnummer(varsel.fodselsnummer)
            .withGrupperingsId(varsel.groupId)
            .withTekst(varsel.message)
            .withLink(varsel.link)
            .withSikkerhetsnivaa(varsel.sikkerhetsnivaa)
            .withPrefererteKanaler(PreferertKanal.SMS)

        if (varsel.visibleUntil != null) {
            beskjedBuilder.withSynligFremTil(varsel.visibleUntil)
        }

        val beskjed = beskjedBuilder.build()

        beskjedProducer.send(beskjedTopic, nokkel, beskjed).addCallback(
            {
                logger.info("Sent Beskjed on topic $beskjedTopic")
                callback.onSuccess()
            },
            { callback.onFailure(it) }
        )
    }

    private fun sendOppgave(varsel: Varsel, callback: Callback) {
        val nokkel = Nokkel(systemUser, varsel.getSystemId())

        val oppgave = OppgaveBuilder()
            .withTidspunkt(LocalDateTime.now())
            .withFodselsnummer(varsel.fodselsnummer)
            .withGrupperingsId(varsel.groupId)
            .withTekst(varsel.message)
            .withLink(varsel.link)
            .withSikkerhetsnivaa(varsel.sikkerhetsnivaa)
            .withPrefererteKanaler(PreferertKanal.SMS)
            .build()

        oppgaveProducer.send(oppgaveTopic, nokkel, oppgave).addCallback(
            {
                logger.info("Sent Oppgave on topic $beskjedTopic")
                callback.onSuccess()
            },
            {
                callback.onFailure(it)
            }
        )
    }
}
