package no.nav.fo.veilarbvarsel.config

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*
import java.util.concurrent.Future

class KafkaRecordProducer (
    host: String,
    port: Int
) {

    val producer: Producer<String, String>

    init {
        val props = Properties()
        props["bootstrap.servers"] = "$host:$port"
        props["key.serializer"] = StringSerializer::class.java
        props["value.serializer"] = StringSerializer::class.java

        this.producer = KafkaProducer(props)
    }

    fun send(topic: String, key: String, value: String): Future<RecordMetadata> {
        val record = ProducerRecord(topic, key, value)
        return producer.send(record)
    }
}