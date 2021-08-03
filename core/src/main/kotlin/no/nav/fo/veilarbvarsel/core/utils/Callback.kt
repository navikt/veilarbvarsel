package no.nav.fo.veilarbvarsel.core.utils

interface Callback {

    fun onSuccess()

    fun onFailure(exception: Throwable)

}
