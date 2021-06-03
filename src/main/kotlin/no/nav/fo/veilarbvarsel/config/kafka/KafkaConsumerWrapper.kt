package no.nav.fo.veilarbvarsel.config.kafka

import no.nav.common.kafka.consumer.KafkaConsumerClient
import no.nav.common.kafka.consumer.TopicConsumer
import no.nav.common.kafka.consumer.util.ConsumerUtils.jsonConsumer
import no.nav.common.kafka.consumer.util.KafkaConsumerClientBuilder
import no.nav.common.kafka.util.KafkaPropertiesPreset.onPremDefaultConsumerProperties
import no.nav.common.utils.NaisUtils.getCredentials
import no.nav.fo.veilarbvarsel.config.KafkaEnvironment
import no.nav.fo.veilarbvarsel.config.kafka.utils.KafkaEventDeserializer
import no.nav.fo.veilarbvarsel.config.system.features.ClosableJob
import no.nav.fo.veilarbvarsel.varsel.Test
import no.nav.fo.veilarbvarsel.varsel.VarselEvent
import no.nav.fo.veilarbvarsel.varsel.VarselEventConsumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*

abstract class KafkaConsumerWrapper<K, V>(
    env: KafkaEnvironment,
    systemUser: String,
    private val topics: String
) : ClosableJob {
    val CONSUMER_GROUP_ID = "veilarbvarsel-consumer"


    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val props = Properties()

    val consumerClient: KafkaConsumerClient<K, V>

    private var shutdown = false
    private var running = false

    init {

        val credentials = getCredentials("service_user")

        consumerClient = KafkaConsumerClientBuilder.builder<K, V>()
            .withProps(onPremDefaultConsumerProperties(CONSUMER_GROUP_ID, env.bootstrapServers, credentials))
            .build()


        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = env.bootstrapServers
        props["group.id"] = systemUser
        props["key.deserializer"] = StringDeserializer::class.java
        props["value.deserializer"] = KafkaEventDeserializer::class.java
        props["max.poll.records"] = 1
        props["max.partition.fetch.bytes"] = 1048576 / 2
        props["auto.offset.reset"] = "earliest"
    }

    abstract fun handle(data: V)


    fun topicConsumers(): Map<String, TopicConsumer<K, V>> {
        val map = mutableMapOf<String, TopicConsumer<K, V>>()

        val handler = Test()
        map[topics] = jsonConsumer(VarselEvent::class.java, handler::handle)

        return map
    }

    override fun run() {
        logger.info("Starting Kafka Consumer on topics $topics")

        consumerClient.start()

//        running = true
//
//        val consumer = KafkaConsumer<K, V>(props).apply {
//            subscribe(topics)
//        }
//
//        consumer.use {
//            while (!shutdown) {
//                val records = consumer.poll(Duration.ofMillis(5000))
//
//                logger.info("Getting records from $topics. size: ${records.count()}")
//
//                records.iterator().forEach {
//                    handle(it.value())
//                }
//            }
//        }
//
//        consumer.close()
//        running = false
    }

    override fun close() {
        logger.info("Closing Kafka Consumer on topics $topics...")

        consumerClient.stop()

//        shutdown = true
//        while (running) {
//            Thread.sleep(100)
//        }

        logger.info("Kafka Consumer on topics $topics closed!")
    }
}
