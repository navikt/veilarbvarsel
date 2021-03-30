package no.nav.fo.veilarbvarsel.mq

import java.io.StringWriter
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBElement
import javax.xml.bind.Marshaller
import javax.xml.transform.stream.StreamResult

abstract class MQProducer<T> {

    fun marshall(e: JAXBElement<T>, context: JAXBContext): String {
        val writer = StringWriter()
        val marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true)
        marshaller.marshal(e, StreamResult(writer))

        return writer.toString()
    }

}