package no.nav.fo.veilarbvarsel.it.utils

import javax.jms.ConnectionFactory

class MqConsumer(
    val connectionFactory: ConnectionFactory,
    val queue: String) {

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