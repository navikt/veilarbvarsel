package no.nav.fo.veilarbvarsel.server.kafka

import no.nav.fo.veilarbvarsel.server.kafka.utils.KafkaJsonSerializer
import no.nav.fo.veilarbvarsel.server.kafka.utils.KafkaCallback
import org.apache.kafka.clients.producer.*
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import java.util.*

interface KafkaMessageProducer<K, V> {

    fun send(topic: String, key: K, value: V, callback: KafkaCallback)

}

class KafkaProducer<K, V>(
    host: String,
    port: Int
): KafkaMessageProducer<K, V> {

    val producer: Producer<K, V>
    val logger = LoggerFactory.getLogger(this.javaClass)

    init {
        val props = Properties()
        props["bootstrap.servers"] = "$host:$port"
        props["key.serializer"] = StringSerializer::class.java
        props["value.serializer"] = KafkaJsonSerializer::class.java

        this.producer = KafkaProducer(props)

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                logger.info("Shutting down Kafka Producer gracefully...")
                producer.flush()
                producer.close()
                logger.info("Kafka Produser has shut down!")
            }
        })
    }


    override fun send(topic: String, key: K, value: V, callback: KafkaCallback) {
        val record = ProducerRecord(topic, key, value)

        producer.send(record) { _, exception ->
            if (exception == null) {
                callback.onSuccess()
            } else {
                callback.onFailure(exception)
            }
        }
    }

}