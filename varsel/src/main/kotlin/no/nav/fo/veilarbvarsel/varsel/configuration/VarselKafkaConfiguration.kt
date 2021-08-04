package no.nav.fo.veilarbvarsel.varsel.configuration

import no.nav.common.kafka.util.KafkaPropertiesPreset
import no.nav.common.utils.NaisUtils
import no.nav.fo.veilarbvarsel.varsel.events.VarselEvent
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer


@EnableKafka
@Configuration
class VarselKafkaConfiguration {

    @Value("\${spring.kafka.consumer.group-id}")
    private lateinit var groupId: String

    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var brokers: String

    @Bean
    fun consumerFactory(): ConsumerFactory<String, VarselEvent> {
        return DefaultKafkaConsumerFactory(properties(), StringDeserializer(), JsonDeserializer(VarselEvent::class.java))
    }

    @Bean
    fun kafkaListenerContainerFactory(): KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String?, VarselEvent?>>? {
        val factory: ConcurrentKafkaListenerContainerFactory<String, VarselEvent> =
            ConcurrentKafkaListenerContainerFactory<String, VarselEvent>()
        factory.consumerFactory = consumerFactory()
        return factory
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, VarselEvent> {
        return DefaultKafkaProducerFactory(properties())
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, VarselEvent> {
        return KafkaTemplate(producerFactory())
    }

    private fun properties(): Map<String, Any> {
        val credentials = NaisUtils.getCredentials("service_user")
        val properties = KafkaPropertiesPreset.onPremDefaultProducerProperties("VEILARBVARSEL", brokers, credentials)

        properties[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = brokers
        properties[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        properties[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java

        return properties.map { it.key.toString() to it.value }.toMap()
    }
}
