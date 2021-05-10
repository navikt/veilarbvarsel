package no.nav.fo.veilarbvarsel.config.kafka.utils

interface KafkaCallback {
    fun onSuccess()

    fun onFailure(exception: Exception)
}
