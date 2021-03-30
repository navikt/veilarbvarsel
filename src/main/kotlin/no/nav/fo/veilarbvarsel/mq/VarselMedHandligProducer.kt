package no.nav.fo.veilarbvarsel.mq

import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.AktoerId
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.ObjectFactory
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.Parameter
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.VarselMedHandling
import javax.jms.ConnectionFactory
import javax.xml.bind.JAXBContext

val context = JAXBContext.newInstance(VarselMedHandling::class.java)

class VarselMedHandligProducer(val connectionFactory: ConnectionFactory) : MQProducer<VarselMedHandling>() {

    val PARAGAF8_VARSEL_ID = "DittNAV_000008"

    val queue = System.getenv("VARSEL_MED_HANDLING_MQ") ?: "DEV.QUEUE.1"

    fun send(aktorId: String, varselbestillingId: String) {
        val mottaker = AktoerId()
        mottaker.aktoerId = aktorId

        val varselMedHandling = VarselMedHandling()
        varselMedHandling.varseltypeId = PARAGAF8_VARSEL_ID
        varselMedHandling.isReVarsel = false
        varselMedHandling.mottaker = mottaker
        varselMedHandling.varselbestillingId = varselbestillingId

        val parameter = Parameter()
        parameter.key = "varselbestillingId"
        parameter.value = varselbestillingId

        varselMedHandling
            .parameterListe
            .add(parameter)

        val connection = connectionFactory.createConnection()
        connection.start()

        val session = connection.createSession()

        val message = session.createTextMessage(
            marshall(
                ObjectFactory().createVarselMedHandling(varselMedHandling),
                context
            )
        )

        val destination = session.createQueue(queue)
        session.createProducer(destination).send(message)

        connection.close()

    }
}