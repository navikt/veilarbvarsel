package no.nav.fo.veilarbvarsel

import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.fo.veilarbvarsel.db.TestSchema
import no.nav.fo.veilarbvarsel.kafka.consumer.KafkaConsumeExecutor
import no.nav.fo.veilarbvarsel.kafka.consumer.KafkaConsumerRegistry
import no.nav.fo.veilarbvarsel.mq.MQConfiguration
import no.nav.fo.veilarbvarsel.mq.producer.VarselMedHandligProducer
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun main() {
    val producer = VarselMedHandligProducer(MQConfiguration.connectionFactory())
    producer.send("1", UUID.randomUUID().toString())

    val port = System.getenv("PORT")?.toInt() ?: 8080
    val server = embeddedServer(Netty, port, module = Application::mainModule)
    server.start()
}

fun Application.mainModule() {

//    DB.connect()
//
//    transaction {
//        SchemaUtils.create(TestSchema)
//    }
//
//    setupConsumer()
//    val producer = KafkaRecordProducer()
//
//    for(i in 0..100) {
//        producer.send("Topic1", "oppfolgingsperiode_x", "Melding $i")
//        Thread.sleep(500)
//    }
//
//    println("Test")
}

fun setupConsumer() {
    KafkaConsumerRegistry
        .register(KafkaConsumeExecutor(listOf("Topic1")))
        .start()
}

fun insert(name: String, age: Int?): Int {
    val id = transaction {
        TestSchema.insertAndGetId { t ->
            t[TestSchema.name] = name
            if (age != null) {
                t[TestSchema.age] = age
            }
        }
    }

    return id.value
}
