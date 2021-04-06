package no.nav.fo.veilarbvarsel.it

import no.nav.fo.veilarbvarsel.it.utils.MqConsumer
import no.nav.fo.veilarbvarsel.mq.MQConfiguration
import org.junit.ClassRule
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.utility.DockerImageName
import javax.jms.Message

class MoteSmsIT {

    @ClassRule
    val kafka: KafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"))

    @ClassRule
    val mq: RabbitMQContainer = RabbitMQContainer(DockerImageName.parse("rabbitmq"))

    @Test
    @DisplayName("Receive meeting message on kafka, sends out meeting mq")
    internal fun sendMoteInvitation() {
        val mqConsumer = object: MqConsumer(MQConfiguration.connectionFactory(), "queue") {

            override fun handle(message: Message) {
                println(message) //TODO Handle
            }

        }

        mqConsumer.start()

        //val kafkaProducer = KafkaRecordProducer()

        //kafkaProducer.send("", "", "")

        mqConsumer.stop()
    }
}
