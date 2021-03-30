package no.nav.fo.veilarbvarsel

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.fo.veilarbvarsel.db.DB
import no.nav.fo.veilarbvarsel.db.TestSchema
import no.nav.fo.veilarbvarsel.kafka.consumer.KafkaConsumeExecutor
import no.nav.fo.veilarbvarsel.kafka.consumer.KafkaConsumerRegistry
import no.nav.fo.veilarbvarsel.kafka.consumer.KafkaRecordConsumer
import no.nav.fo.veilarbvarsel.kafka.producer.KafkaRecordProducer
import no.nav.fo.veilarbvarsel.mq.MQConfiguration
import no.nav.fo.veilarbvarsel.mq.VarselMedHandligProducer
import no.nav.fo.veilarbvarsel.mq.VarselMedHandlingConsumer
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import java.util.logging.Level

fun main() {
    val consumer = VarselMedHandlingConsumer(MQConfiguration.connectionFactory()).consume()
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