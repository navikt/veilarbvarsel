package no.nav.fo.veilarbvarsel.kafka.consumer

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.header.Header

class KafkaConsumeExecutor(topics: List<String>)
    : KafkaRecordConsumer(topics) {

    override fun executor(record: ConsumerRecord<String, String>): Runnable {
        return Runnable {
            run {
                Thread.sleep(1000)
                printHeaders(record)
                val threadId = Thread.currentThread().id
                println("(Thread $threadId) Message: ${record.value()}")
            }
        }
    }

    private fun printHeaders(record: ConsumerRecord<String, String>) {
        for (header: Header in record.headers()) {
            println("${header.key()}: ${String(header.value())}")
        }
    }
}