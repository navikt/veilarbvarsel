package no.nav.fo.veilarbvarsel.kafka.domain

import java.lang.Exception
import java.util.*

interface VarselCallback {

    fun onSuccess(id: UUID)

    fun onFailure(exception: Exception) {
        // Implemented base version as onFailure is not always needed.
    }

}