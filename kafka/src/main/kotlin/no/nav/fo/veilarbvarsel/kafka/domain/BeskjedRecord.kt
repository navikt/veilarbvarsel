package no.nav.fo.veilarbvarsel.kafka.domain

import no.nav.brukernotifikasjon.schemas.Beskjed
import no.nav.brukernotifikasjon.schemas.builders.BeskjedBuilder
import java.net.URL
import java.time.LocalDateTime

data class BeskjedRecord(
    val personIdent: String,
    val gruppeId: String?,
    val text: String?,
    val link: URL?,
    val securityLevel: Int,
    val visibleUntil: LocalDateTime
) {

    fun toBeskjed(): Beskjed {
        return BeskjedBuilder()
            .withTidspunkt(LocalDateTime.now())
            .withFodselsnummer(personIdent)
            .withGrupperingsId(gruppeId)
            .withTekst(text)
            .withLink(link)
            .withSikkerhetsnivaa(securityLevel)
            .withSynligFremTil(visibleUntil)
            .build()
    }

}