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

    protected fun marshallAndSend(callId: String, e: JAXBElement<T>) {
        mqSend(marshall(e, context), callId)
    }

    protected fun marshallAndSend(callId: String, e: T) {
        mqSend(marshall(e, context), callId)
    }

    private fun mqSend(messageString: String, varselid: String) {
        val connection = connectionFactory.createConnection()
        connection.start()
        val session = connection.createSession()

        val message = session.createTextMessage(messageString)
        message.setStringProperty("callId", varselid)

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

    private fun marshall(e: T, context: JAXBContext): String {
        val writer = StringWriter()
        val marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true)
        marshaller.marshal(e, StreamResult(writer))

        return writer.toString()
    }

}