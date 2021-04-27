package no.nav.fo.veilarbvarsel.kafka.domain

import no.nav.brukernotifikasjon.schemas.Oppgave
import no.nav.brukernotifikasjon.schemas.builders.OppgaveBuilder
import java.net.URL
import java.time.LocalDateTime

data class OppgaveRecord(
    val personIdent: String,
    val gruppeId: String,
    val text: String,
    val link: URL,
    val sikkerhetsnivaa: Int,
) {

    fun toOppgave(): Oppgave {
        return OppgaveBuilder()
            .withTidspunkt(LocalDateTime.now())
            .withFodselsnummer(personIdent)
            .withGrupperingsId(gruppeId)
            .withTekst(text)
            .withLink(link)
            .withSikkerhetsnivaa(sikkerhetsnivaa)
            .build()
    }
}
