package no.nav.fo.veilarbvarsel.kafka

import no.nav.fo.veilarbvarsel.kafka.utils.KafkaCallback
import no.nav.fo.veilarbvarsel.kafka.utils.KafkaJsonSerializer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import java.util.*

interface KafkaMessageProducer<K, V> {
    fun send(topic: String, key: K, value: V, callback: KafkaCallback?)
}

class KafkaProducer<K, V> : KafkaMessageProducer<K, V> {

    val producer: Producer<K, V>
    val logger = LoggerFactory.getLogger(this.javaClass)

    init {
        val host = System.getenv("KAFKA_HOST") ?: "localhost"
        val port = System.getenv("KAFKA_PORT") ?: 29092

        val properties = Properties()

        properties[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "$host:$port"
        properties[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        properties[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaJsonSerializer::class.java

        this.producer = KafkaProducer(properties)

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                logger.info("Shutting down Kafka Producer gracefully...")
                producer.flush()
                producer.close()
                logger.info("Kafka Produser has shut down!")
            }
        })
    }


    override fun send(topic: String, key: K, value: V, callback: KafkaCallback?) {
        val record = ProducerRecord(topic, key, value)

        producer.send(record) { _, exception ->
            if (exception == null) {
                callback?.onSuccess()
            } else {
                callback?.onFailure(exception)
            }
        }
    }

}