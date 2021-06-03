package no.nav.fo.veilarbvarsel.config.kafka

import no.nav.fo.veilarbvarsel.config.KafkaEnvironment
import no.nav.fo.veilarbvarsel.config.kafka.utils.KafkaEventDeserializer
import no.nav.fo.veilarbvarsel.config.system.features.ClosableJob
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*

abstract class KafkaConsumerWrapper<K, V>(
    env: KafkaEnvironment,
    systemUser: String,
    private val topics: List<String>
) : ClosableJob {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val props = Properties()

    private var shutdown = false
    private var running = false

    init {
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = env.bootstrapServers
        props["group.id"] = systemUser
        props["key.deserializer"] = StringDeserializer::class.java
        props["value.deserializer"] = KafkaEventDeserializer::class.java
        props["max.poll.records"] = 1
        props["max.partition.fetch.bytes"] = 1048576 / 2
        props["auto.offset.reset"] = "earliest"
    }

    abstract fun handle(data: V)

    override fun run() {
        logger.info("Starting Kafka Consumer on topics $topics")

        running = true

        val consumer = KafkaConsumer<K, V>(props).apply {
            subscribe(topics)
        }

        consumer.use {
            while (!shutdown) {
                val records = consumer.poll(Duration.ofMillis(5000))

                logger.info("Getting records from $topics. size: ${records.count()}")

                records.iterator().forEach {
                    handle(it.value())
                }
            }
        }

        consumer.close()
        running = false
    }

    override fun close() {
        logger.info("Closing Kafka Consumer on topics $topics...")

        shutdown = true
        while (running) {
            Thread.sleep(100)
        }

        logger.info("Kafka Consumer on topics $topics closed!")
    }
}
