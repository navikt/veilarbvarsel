package no.nav.fo.veilarbvarsel.mq.producer

import no.nav.melding.virksomhet.stopprevarsel.v1.stopprevarsel.ObjectFactory
import no.nav.melding.virksomhet.stopprevarsel.v1.stopprevarsel.StoppReVarsel
import javax.jms.ConnectionFactory
import javax.xml.bind.JAXBContext

class StoppRevarslingProducer(connectionFactory: ConnectionFactory) : MQProducer<StoppReVarsel>(
    connectionFactory,
    JAXBContext.newInstance(StoppReVarsel::class.java),
    System.getenv("STOPP_REVARSEL_MQ") ?: "DEV.QUEUE.2"
) {

    fun send(varselbestillingId: String) {
        val stoppReVarsel = StoppReVarsel()
        stoppReVarsel.varselbestillingId = varselbestillingId

        marshallAndSend(varselbestillingId, ObjectFactory().createStoppReVarsel(stoppReVarsel))
    }

}