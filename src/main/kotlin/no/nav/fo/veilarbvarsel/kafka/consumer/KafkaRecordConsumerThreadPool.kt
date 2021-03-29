package no.nav.fo.veilarbvarsel.kafka.consumer

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object KafkaRecordConsumerThreadPool {

    val executor: ExecutorService

    init {
        executor = Executors.newFixedThreadPool(5)
    }

    fun execute(runner: Runnable) {
        executor.execute(runner)
    }
}