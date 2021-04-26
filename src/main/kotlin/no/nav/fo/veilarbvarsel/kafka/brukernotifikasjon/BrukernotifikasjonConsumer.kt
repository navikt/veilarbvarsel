package no.nav.fo.veilarbvarsel.kafka.doknotifikasjon

import no.nav.fo.veilarbvarsel.features.ClosableJob
import no.nav.fo.veilarbvarsel.varsel.VarselService

class BrukerNotifikasjonConsumer(val service: VarselService): ClosableJob {



    override fun close() {
        TODO("Not yet implemented")
    }

    override fun run() {
        TODO("Not yet implemented")
    }
}