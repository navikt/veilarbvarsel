package no.nav.fo.veilarbvarsel.kafka.utils

import java.lang.Exception

interface KafkaCallback {
    fun onSuccess()

    fun onFailure(exception: Exception)
}