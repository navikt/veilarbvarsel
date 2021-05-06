package no.nav.fo.veilarbvarsel.kafka.utils

interface KafkaCallback {
    fun onSuccess()

    fun onFailure(exception: Exception)
}