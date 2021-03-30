package no.nav.fo.veilarbvarsel.mq.producer

import java.io.StringWriter
import javax.jms.ConnectionFactory
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBElement
import javax.xml.bind.Marshaller
import javax.xml.transform.stream.StreamResult

//TODO Add pooling of connections
abstract class MQProducer<T>(
    private val connectionFactory: ConnectionFactory,
    private val context: JAXBContext,
    private val queueName: String
) {


    protected fun sendToMq(e: JAXBElement<T>) {
        val connection = connectionFactory.createConnection()
        connection.start()
        val session = connection.createSession()
        val message = session.createTextMessage(marshall(e, context))

        val destination = session.createQueue(queueName)
        session.createProducer(destination).send(message)

        connection.close()

    }

    private fun marshall(e: JAXBElement<T>, context: JAXBContext): String {
        val writer = StringWriter()
        val marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true)
        marshaller.marshal(e, StreamResult(writer))

        return writer.toString()
    }

}