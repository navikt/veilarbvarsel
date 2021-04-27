package no.nav.fo.veilarbvarsel.kafka

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*

abstract class KafkaConsumer<T>(
    val topics: List<String>
) {

    val props = Properties()
    var running = true
    lateinit var job: Job

    val logger = LoggerFactory.getLogger(this.javaClass)

    init {
        props["bootstrap.servers"] = "localhost:9092"
        props["group.id"] = "veilarb-varsel"
        props["key.deserializer"] = StringDeserializer::class.java
        props["value.deserializer"] = StringDeserializer::class.java

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                logger.info("Shutting down Kafka Consumer gracefully...")
                running = false

                while (job.isActive) {
                    sleep(100)
                }

                logger.info("Kafka Consumer4 has shut down!")
            }
        })
    }

    fun start() {
        running = true

        job = GlobalScope.launch {
            val consumer = KafkaConsumer<String, T>(props).apply {
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

    fun isHealthy(): Boolean {
        return !job.isCancelled
    }

    abstract fun handle(record: ConsumerRecord<String, T>)
}