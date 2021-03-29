package no.nav.fo.veilarbvarsel.kafka.consumer

object KafkaConsumerRegistry {

    val consumers: MutableList<KafkaRecordConsumer> = mutableListOf()
    private var worker: Thread?
    private var running: Boolean

    init {
        worker = null
        running = false
    }

    fun register(consumer: KafkaRecordConsumer): KafkaConsumerRegistry {
        this.consumers.add(consumer)
        return this
    }

    fun start() {
        this.running = true

        if(this.worker == null) {
            worker = Thread {
                while(running) {
                    consumers.forEach {
                        it.consume()
                    }
                }
            }

            worker!!.start()
        }
    }

    fun stop() {
        this.running = false
        this.worker = null
    }
}