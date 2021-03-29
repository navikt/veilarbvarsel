package no.nav.fo.veilarbvarsel.kafka.producer

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*
import java.util.concurrent.Future

class KafkaRecordProducer {

    val producer: Producer<String, String>

    constructor() {
        val props = Properties()
        props["bootstrap.servers"] = "localhost:9092"
        props["key.serializer"] = StringSerializer::class.java
        props["value.serializer"] = StringSerializer::class.java

        this.producer = KafkaProducer(props)
    }

    fun send(topic: String, key: String, value: String): Future<RecordMetadata> {
        val record = ProducerRecord(topic, key, value)
        return producer.send(record)
    }

}