package no.nav.fo.veilarbvarsel.mq.producer

import no.nav.melding.virksomhet.varsel.v1.varsel.XMLAktoerId
import no.nav.melding.virksomhet.varsel.v1.varsel.XMLVarsel
import no.nav.melding.virksomhet.varsel.v1.varsel.XMLVarslingstyper
import javax.jms.ConnectionFactory
import javax.xml.bind.JAXBContext

private const val VARSEL_ID = "DittNAV_000007"

class ServiceMeldingProducer(connectionFactory: ConnectionFactory) : MQProducer<XMLVarsel>(
    connectionFactory,
    JAXBContext.newInstance(XMLVarsel::class.java),
    System.getenv("SERVICE_MELDING_MQ") ?: "DEV.QUEUE.3"
) {

    fun send(callId: String, aktoerId: String) {
        val xmlVarsel = XMLVarsel()
        xmlVarsel.mottaker = XMLAktoerId().withAktoerId(aktoerId)
        xmlVarsel.varslingstype = XMLVarslingstyper(VARSEL_ID, null, null)

        marshallAndSend(callId, xmlVarsel)
    }

}