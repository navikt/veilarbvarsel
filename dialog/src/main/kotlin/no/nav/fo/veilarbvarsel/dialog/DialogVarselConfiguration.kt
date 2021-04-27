package no.nav.fo.veilarbvarsel.dialog

import no.nav.fo.veilarbvarsel.dialog.domain.dao.MeldingDAO
import no.nav.fo.veilarbvarsel.kafka.KafkaConsumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DialogVarselConfiguration {

    lateinit var consumer: KafkaConsumer<String>

    fun initialize() {
        initKafka()
        initDatabase()
    }

    fun initKafka() {
        this.consumer = setupKafkaConsumer()
    }

    fun initDatabase() {
        transaction {
            SchemaUtils.create(MeldingDAO)
        }
    }

    private fun setupKafkaConsumer(): KafkaConsumer<String> {
        return object : KafkaConsumer<String>(listOf("Topic1")) {

            override fun handle(record: ConsumerRecord<String, String>) {
                val threadId = Thread.currentThread().id
                println("(Thread $threadId) Message: ${record.value()}")
            }

        }
    }

}