package no.nav.fo.veilarbvarsel.server

import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.fo.veilarbvarsel.server.domain.VarselType
import no.nav.fo.veilarbvarsel.server.domain.dao.VarselDAO
import no.nav.fo.veilarbvarsel.server.domain.dao.VarselEventDAO
import no.nav.fo.veilarbvarsel.server.domain.kafka.internal.CreateVarselPayload
import no.nav.fo.veilarbvarsel.server.domain.kafka.internal.EventType
import no.nav.fo.veilarbvarsel.server.domain.kafka.internal.InternalEvent
import no.nav.fo.veilarbvarsel.server.kafka.KafkaInternalConsumer
import no.nav.fo.veilarbvarsel.server.kafka.KafkaProducer
import no.nav.fo.veilarbvarsel.server.kafka.utils.KafkaCallback
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.LocalDateTime
import java.util.*

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    val server = embeddedServer(Netty, port, module = Application::server)
    server.start()

//    val consumer = object : KafkaRecordConsumerTwo(listOf("Topic1")) {
//
//        override fun handle(record: ConsumerRecord<String, String>) {
//            val threadId = Thread.currentThread().id
//            println("(Thread $threadId) Message: ${record.value()}")
//            throw UnsatisfiedLinkError("kake")
//        }
//
//    }
//    consumer.start()
//    val producer = KafkaRecordProducer("localhost", 9092)
//
//    for(i in 0..100) {
//        producer.send("Topic1", "oppfolgingsperiode_x", "Melding $i")
//        println(consumer.isHealthy())
//        Thread.sleep(2000)
//    }
//
//    println("DONE SENDING")
//    Thread.sleep(100000)

}

fun Application.server() {
    DB.connect()

    transaction {
        //TODO REMOVE DROPS
        SchemaUtils.drop(VarselEventDAO)
        SchemaUtils.drop(VarselDAO)

        SchemaUtils.create(VarselDAO)
        SchemaUtils.create(VarselEventDAO)
    }

    val service = VarselServiceImpl()
    val internalKafkaConsumer = setupInternalKafkaConsumer(service)
    val kafkaProducer = KafkaProducer<String, InternalEvent>("localhost", 9092)


    val message = InternalEvent(
        UUID.randomUUID(),
        LocalDateTime.now(),
        "VARSEL",
        EventType.CREATE,
        CreateVarselPayload(
            UUID.randomUUID().toString(),
            VarselType.OPPGAVE,
            "12345678910",
            "g1",
            "Dette er en melding",
            4,
            null
        )
    )

    kafkaProducer.send("test-topic", "varsel", message, object: KafkaCallback {

        override fun onSuccess() {
            println("Sent message $message")
        }
    })


    println()

    //DialogVarselConfiguration.initialize()

    println("TEST")
}

fun setupInternalKafkaConsumer(service: VarselService): KafkaInternalConsumer {
    val consumer = object: KafkaInternalConsumer("test-topic") {

        override fun handle(data: InternalEvent) = when (data.payload) {
            is CreateVarselPayload -> service.add(data.payload.toVarsel())
        }

    }
    consumer.start()

    return consumer
}

//fun testMq() {
//    val producer = VarselMedHandligProducer(MQConfiguration.connectionFactory())
//    producer.send("1", UUID.randomUUID().toString())
//
//    val port = System.getenv("PORT")?.toInt() ?: 8080
//    val server = embeddedServer(Netty, port, module = Application::mainModule)
//    server.start()
//}
