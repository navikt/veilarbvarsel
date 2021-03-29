package no.nav.fo.veilarbvarsel.kafka.consumer

import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.util.*

abstract class KafkaRecordConsumer {

    val consumer: Consumer<String, String>

    constructor(topics: List<String>) {
        val props = Properties()
        props["bootstrap.servers"] = "localhost:9092"
        props["group.id"] = "veilarb-varsel"
        props["key.deserializer"] = StringDeserializer::class.java
        props["value.deserializer"] = StringDeserializer::class.java

        this.consumer = KafkaConsumer(props)
        consumer.subscribe(topics)
    }

    fun consume() {
        val records = consumer.poll(Duration.ofSeconds(1))

        records.iterator().forEach {
            KafkaRecordConsumerThreadPool.execute(executor(it))
        }
    }

    abstract fun executor(record: ConsumerRecord<String, String>): Runnable
}