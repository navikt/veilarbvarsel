package no.nav.fo.veilarbvarsel.kafka

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import java.util.*

interface KafkaMessageProducer<K, V> {
    fun send(topic: String, key: K, value: V, callback: Callback)
}

class KafkaProducerImpl<K, V>(
    host: String,
    port: Int
): KafkaMessageProducer<K, V> {

    val producer: Producer<K, V>
    val logger = LoggerFactory.getLogger(this.javaClass)

    init {
        val props = Properties()
        props["bootstrap.servers"] = "$host:$port"
        props["key.serializer"] = KafkaAvroSerializer::class.java
        props["value.serializer"] = KafkaAvroSerializer::class.java
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081/");

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

    override fun send(topic: String, key: K, value: V, callback: Callback) {
        val record = ProducerRecord(topic, key, value)
        producer.send(record, callback)
    }

}