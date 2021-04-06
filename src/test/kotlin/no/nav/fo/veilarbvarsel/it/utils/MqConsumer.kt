package no.nav.fo.veilarbvarsel.it.utils

import javax.jms.Connection
import javax.jms.ConnectionFactory
import javax.jms.Message

abstract class MqConsumer(
    connectionFactory: ConnectionFactory,
    val queue: String) {

    private val connection: Connection = connectionFactory.createConnection()

    fun start() {
        val session = connection.createSession()
        val destination = session.createQueue(queue)

        session.createConsumer(destination).setMessageListener {
            handle(it)
        }

        connection.start()
    }

    fun stop() {
        connection.stop()
    }

    abstract fun handle(message: Message)
}