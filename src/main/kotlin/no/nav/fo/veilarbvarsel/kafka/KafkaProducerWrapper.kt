package no.nav.fo.veilarbvarsel.kafka

import no.nav.fo.veilarbvarsel.kafka.utils.KafkaCallback
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import java.util.*

class KafkaProducerWrapper<K, V>(
    properties: Properties,
    private val topic: String
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val producer: KafkaProducer<K, V> = KafkaProducer(properties)

    fun send(key: K, event: V, callback: KafkaCallback?) {
        ProducerRecord(topic, key, event).let {
            producer.send((it)) { _, exception ->
                if (exception == null) {
                    callback?.onSuccess()
                } else {
                    callback?.onFailure(exception)
                        ?: throw InternalError("Could not send message", exception)
                }
            }
        }
    }

    fun flushAndClose() {
        try {
            producer.flush()
            producer.close()
            log.info("Produsent for kafka-eventer er flushet og lukket.")
        } catch (e: Exception) {
            log.warn("Klarte ikke å flushe og lukke produsent. Det kan være eventer som ikke ble produsert.")
        }
    }


}