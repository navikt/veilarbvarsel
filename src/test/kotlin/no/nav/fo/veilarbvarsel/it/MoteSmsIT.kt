package no.nav.fo.veilarbvarsel.it

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


import org.junit.ClassRule
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.utility.DockerImageName





class MoteSmsIT {

    @ClassRule
    val kafka: KafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"))

    @ClassRule
    val mq: RabbitMQContainer = RabbitMQContainer(DockerImageName.parse("rabbitmq"))



    @Test
    @DisplayName("Receive meeting message on kafka, sends out meeting mq")
    internal fun sendMoteInvitation() {

        TODO("Not yet implemented")
    }
}
