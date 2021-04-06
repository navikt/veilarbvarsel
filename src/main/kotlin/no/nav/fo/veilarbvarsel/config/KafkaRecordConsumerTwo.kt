package no.nav.fo.veilarbvarsel.config

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.util.*

abstract class KafkaRecordConsumerTwo(
    val topics: List<String>
) {

    val props = Properties()
    var running = true

    init {
        props["bootstrap.servers"] = "localhost:9092"
        props["group.id"] = "veilarb-varsel"
        props["key.deserializer"] = StringDeserializer::class.java
        props["value.deserializer"] = StringDeserializer::class.java
    }

    fun start() {
        running = true

        GlobalScope.launch {
            val consumer = KafkaConsumer<String, String>(props).apply {
                subscribe(topics)
            }

            consumer.use {
                while(running) {
                    val records = consumer.poll(Duration.ofMillis(100))

                    records.iterator().forEach {
                        handle(it)
                    }
                }
            }
        }
    }

    fun stop() {
        running = false
    }

    abstract fun handle(record: ConsumerRecord<String, String>)
}