package no.nav.fo.veilarbvarsel.varsel

import no.nav.fo.veilarbvarsel.config.KafkaEnvironment
import no.nav.fo.veilarbvarsel.config.kafka.KafkaProducerWrapper
import no.nav.fo.veilarbvarsel.config.kafka.utils.KafkaCallback
import no.nav.fo.veilarbvarsel.config.kafka.utils.KafkaJsonSerializer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import java.io.Closeable
import java.util.*

class VarselEventProducer(
    private val env: KafkaEnvironment,
    topic: String
) : Closeable {

    private val producer: KafkaProducerWrapper<String, VarselEvent> = KafkaProducerWrapper(getProperties(), topic)

    fun send(varselEvent: VarselEvent, callback: KafkaCallback? = null) {
        producer.send("VARSEL", varselEvent, callback)
    }

    override fun close() {
        producer.flushAndClose()
    }

    private fun getProperties(): Properties {
        val properties = Properties()

        properties[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = env.bootstrapServers
        properties[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        properties[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaJsonSerializer::class.java

        return properties
    }


}
