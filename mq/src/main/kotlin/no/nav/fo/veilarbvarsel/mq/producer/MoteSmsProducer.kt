package no.nav.fo.veilarbvarsel.mq.producer

import no.nav.melding.virksomhet.varsel.v1.varsel.XMLAktoerId
import no.nav.melding.virksomhet.varsel.v1.varsel.XMLParameter
import no.nav.melding.virksomhet.varsel.v1.varsel.XMLVarsel
import no.nav.melding.virksomhet.varsel.v1.varsel.XMLVarslingstyper
import javax.jms.ConnectionFactory
import javax.xml.bind.JAXBContext

private const val VARSEL_ID = "AktivitetsplanMoteVarsel"

class MoteSmsProducer (connectionFactory: ConnectionFactory) : MQProducer<XMLVarsel>(
    connectionFactory,
    JAXBContext.newInstance(XMLVarsel::class.java),
    System.getenv("MOTE_SMS_MQ") ?: "DEV.QUEUE.3"
) {

    fun send(
        varselbestillingId: String,
        aktoerId: String,
        formattedMotetid: String,
        moteType: String,
        aktivitetUrl: String
    ) {

        val xmlVarsel = XMLVarsel()
            .withMottaker(XMLAktoerId().withAktoerId(aktoerId))
            .withVarslingstype(XMLVarslingstyper(VARSEL_ID, null, null))
            .withParameterListes(
                XMLParameter("motedato", formattedMotetid),
                XMLParameter("motetype", moteType),
                XMLParameter("aktiviteturl", aktivitetUrl)
            )

        marshallAndSend(varselbestillingId, xmlVarsel)
    }
}