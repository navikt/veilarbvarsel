package no.nav.fo.veilarbvarsel.mq

import javax.jms.ConnectionFactory

class VarselMedHandlingConsumer(val connectionFactory: ConnectionFactory) {

    val queue = System.getenv("VARSEL_MED_HANDLING_MQ") ?: "DEV.QUEUE.1"

    fun consume() {
        val connection = connectionFactory.createConnection()
        val session = connection.createSession()
        val destination = session.createQueue(queue)
        session.createConsumer(destination).setMessageListener {
            println(it)
        }

        connection.start()
    }
}