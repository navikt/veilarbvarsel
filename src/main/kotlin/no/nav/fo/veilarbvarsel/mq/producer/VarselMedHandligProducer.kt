package no.nav.fo.veilarbvarsel.mq.producer

import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.AktoerId
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.ObjectFactory
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.Parameter
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.VarselMedHandling
import javax.jms.ConnectionFactory
import javax.xml.bind.JAXBContext

val PARAGAF8_VARSEL_ID = "DittNAV_000008"

class VarselMedHandligProducer(connectionFactory: ConnectionFactory) : MQProducer<VarselMedHandling>(
    connectionFactory,
    JAXBContext.newInstance(VarselMedHandling::class.java),
    System.getenv("VARSEL_MED_HANDLING_MQ") ?: "DEV.QUEUE.1"
) {


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

        marshallAndSend(varselbestillingId, ObjectFactory().createVarselMedHandling(varselMedHandling))
    }
}