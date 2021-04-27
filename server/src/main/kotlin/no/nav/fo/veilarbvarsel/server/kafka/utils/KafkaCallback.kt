package no.nav.fo.veilarbvarsel.server.kafka.utils

import java.lang.Exception

interface KafkaCallback {
    fun onSuccess()

    fun onFailure(exception: Exception) {
        // Implemented base version as onFailure is not always needed.
    }
}