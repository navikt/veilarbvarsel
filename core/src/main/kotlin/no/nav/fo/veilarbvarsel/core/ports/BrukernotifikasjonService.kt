package no.nav.fo.veilarbvarsel.core.ports

import no.nav.fo.veilarbvarsel.core.domain.Varsel
import no.nav.fo.veilarbvarsel.core.utils.Callback

interface BrukernotifikasjonService {

    fun sendVarsel(varsel: Varsel, callback: Callback)

    fun sendDone(id: String, fodselsnummer: String, groupId: String, callback: Callback)

}
